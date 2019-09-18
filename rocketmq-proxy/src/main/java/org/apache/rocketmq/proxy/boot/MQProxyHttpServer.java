package org.apache.rocketmq.proxy.boot;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.Pair;
import org.apache.rocketmq.proxy.common.ProtocolKey;
import org.apache.rocketmq.proxy.common.ProtocolVersion;
import org.apache.rocketmq.proxy.common.ProxyResponseCode;
import org.apache.rocketmq.proxy.common.RequestCode;
import org.apache.rocketmq.proxy.common.ServiceState;
import org.apache.rocketmq.proxy.common.command.HttpCommand;
import org.apache.rocketmq.proxy.common.protocol.http.body.Body;
import org.apache.rocketmq.proxy.common.protocol.http.header.Header;
import org.apache.rocketmq.proxy.core.protocol.http.async.AsyncContext;
import org.apache.rocketmq.proxy.core.protocol.http.processor.HttpRequestProcessor;
import org.apache.rocketmq.proxy.utils.ProxyUtil;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQProxyHttpServer extends AbstractHttpServer {

    public Logger logger = LoggerFactory.getLogger(MQProxyHttpServer.class);

    private MQProxyServer mQProxyServer;

    public MQProxyHttpServer(MQProxyServer mQProxyServer) {
        this.mQProxyServer = mQProxyServer;
    }

    protected HashMap<Integer/* request code */, Pair<HttpRequestProcessor, ThreadPoolExecutor>> processorTable =
        new HashMap<Integer, Pair<HttpRequestProcessor, ThreadPoolExecutor>>(64);

    public ServiceState serviceState;

    @Override
    public void start() throws Exception {
        Runnable r = () -> {
            logger.info("MQProxyHttpServer Starting......");
            this.port = mQProxyServer.getProxyConfiguration().httpServerPort;
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                        throws Exception {
                        ch.pipeline()
                            .addLast(new HttpRequestDecoder(),
                                new HttpResponseEncoder(),
                                new HttpObjectAggregator(Integer.MAX_VALUE),
                                new HttpHandler());        // 4
                    }
                }).childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            try {
                logger.info("MQProxyHttpServer[{}] Started......", this.port);
                ChannelFuture future = serverBootstrap.bind(this.port).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                logger.error("MQProxyHttpServer Start Err!", e);
                try {
                    shutdown();
                } catch (Exception e1) {
                    logger.error("MQProxyHttpServer shutdown Err!", e);
                }
                return;
            }

            serviceState = ServiceState.RUNNING;
        };

        Thread t = new Thread(r, "proxy-http-server");
        t.start();
    }

    @Override
    public void init(String threadPrefix) throws Exception {
        logger.info("MQProxyHTTPServer Initialing......");
        super.init(threadPrefix);
        serviceState = ServiceState.INITED;
        logger.info("MQProxyHTTPServer Initialied......");
    }

    @Override
    public void shutdown() throws Exception {
        logger.info("MQProxyHTTPServer Shuting......");
        serviceState = ServiceState.STOPING;
        super.shutdown();
        serviceState = ServiceState.STOPED;
        logger.info("MQProxyHTTPServer Shutdown......");
    }

    public void registerProcessor(Integer requestCode, HttpRequestProcessor processor, ThreadPoolExecutor executor) {
        Preconditions.checkState(ObjectUtils.allNotNull(requestCode), "requestCode can't be null");
        Preconditions.checkState(ObjectUtils.allNotNull(processor), "processor can't be null");
        Preconditions.checkState(ObjectUtils.allNotNull(executor), "executor can't be null");
        Pair<HttpRequestProcessor, ThreadPoolExecutor> pair = new Pair<HttpRequestProcessor, ThreadPoolExecutor>(processor, executor);
        this.processorTable.put(requestCode, pair);
    }

    class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
            HttpPostRequestDecoder decoder = null;
            try {
                if (!httpRequest.decoderResult().isSuccess()) {
                    sendError(ctx, HttpResponseStatus.BAD_REQUEST);
                    return;
                }

                httpRequest.headers().set(ProtocolKey.ClientInstanceKey.IP, RemotingHelper.parseChannelRemoteAddr(ctx.channel()));

                String protocolVersion = StringUtils.deleteWhitespace(httpRequest.headers().get(ProtocolKey.VERSION));
                if (StringUtils.isBlank(protocolVersion)) {
                    protocolVersion = ProtocolVersion.V1.getVersion();
                    httpRequest.headers().set(ProtocolKey.VERSION, ProtocolVersion.V1.getVersion());
                }

                long bodyDecodeStart = System.currentTimeMillis();
                Map<String, Object> bodyMap = new HashMap<>();

                if (httpRequest.method() == HttpMethod.GET) {
                    QueryStringDecoder getDecoder = new QueryStringDecoder(httpRequest.uri());
                    getDecoder.parameters().entrySet().forEach(entry -> {
                        bodyMap.put(entry.getKey(), entry.getValue().get(0));
                    });
                } else if (httpRequest.method() == HttpMethod.POST) {
                    decoder = new HttpPostRequestDecoder(defaultHttpDataFactory, httpRequest);
                    List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
                    for (InterfaceHttpData parm : parmList) {
                        if (parm.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                            Attribute data = (Attribute) parm;
                            bodyMap.put(data.getName(), data.getValue());
                        }
                    }
                } else {
                    sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
                    return;
                }

                String requestCode =
                    (httpRequest.method() == HttpMethod.POST) ? StringUtils.deleteWhitespace(httpRequest.headers().get(ProtocolKey.REQUEST_CODE))
                        : MapUtils.getString(bodyMap, StringUtils.lowerCase(ProtocolKey.REQUEST_CODE), "");

                final HttpCommand requestCommand = new HttpCommand(
                    httpRequest.method().name(),
                    httpRequest.protocolVersion().protocolName(), requestCode);

                HttpCommand responseCommand = null;

                if (!ProtocolVersion.contains(protocolVersion)) {
                    responseCommand = requestCommand.createHttpCommandResponse(ProxyResponseCode.PROXY_PROTOCOL_HEADER_ERR.getRetCode(), ProxyResponseCode.PROXY_PROTOCOL_HEADER_ERR.getErrMsg());
                    sendResponse(ctx, responseCommand.httpResponse());
                    return;
                }

                //check request code
                if (StringUtils.isBlank(requestCode)
                    || !StringUtils.isNumeric(requestCode)
                    || !RequestCode.contains(Integer.valueOf(requestCode))
                    || !processorTable.containsKey(Integer.valueOf(requestCode))) {
                    responseCommand = requestCommand.createHttpCommandResponse(ProxyResponseCode.PROXY_REQUESTCODE_INVALID.getRetCode(), ProxyResponseCode.PROXY_REQUESTCODE_INVALID.getErrMsg());
                    sendResponse(ctx, responseCommand.httpResponse());
                    return;
                }

                if (serviceState == ServiceState.STOPED
                    || serviceState == ServiceState.STOPING) {
                    responseCommand = requestCommand.createHttpCommandResponse(ProxyResponseCode.PROXY_STOP.getRetCode(), ProxyResponseCode.PROXY_STOP.getErrMsg());
                    sendResponse(ctx, responseCommand.httpResponse());
                    return;
                }

                try {
                    requestCommand.setHeader(Header.buildHeader(requestCode, parseHTTPHeader(httpRequest)));
                    requestCommand.setBody(Body.buildBody(requestCode, bodyMap));
                } catch (Exception e) {
                    responseCommand = requestCommand.createHttpCommandResponse(ProxyResponseCode.PROXY_RUNTIME_ERR.getRetCode(), ProxyResponseCode.PROXY_RUNTIME_ERR.getErrMsg() + ProxyUtil.stackTrace(e, 3));
                    sendResponse(ctx, responseCommand.httpResponse());
                    return;
                }

                if (httpLogger.isDebugEnabled()) {
                    httpLogger.debug("{}", requestCommand);
                }

                AsyncContext<HttpCommand> asyncContext = new AsyncContext<HttpCommand>(requestCommand, responseCommand, asyncContextCompleteHandler);
                processProxyRequest(ctx, asyncContext);
            } catch (Exception ex) {
                logger.error("MQProxyHTTPServer.HTTPHandler.channelRead0 err", ex);
            } finally {
                try {
                    decoder.destroy();
                } catch (Exception e) {
                }
            }
        }

        public void processProxyRequest(final ChannelHandlerContext ctx, final AsyncContext<HttpCommand> asyncContext) {
            final Pair<HttpRequestProcessor, ThreadPoolExecutor> choosed = processorTable.get(Integer.valueOf(asyncContext.getRequest().getRequestCode()));
            try {
                choosed.getObject2().submit(() -> {
                    try {
                        if (choosed.getObject1().rejectRequest()) {
                            HttpCommand responseCommand = asyncContext.getRequest().createHttpCommandResponse(ProxyResponseCode.PROXY_REJECT_BY_PROCESSOR_ERROR.getRetCode(), ProxyResponseCode.PROXY_REJECT_BY_PROCESSOR_ERROR.getErrMsg());
                            asyncContext.onComplete(responseCommand);
                            if (asyncContext.isComplete()) {
                                sendResponse(ctx, responseCommand.httpResponse());
                            }
                            return;
                        }

                        choosed.getObject1().processRequest(ctx, asyncContext);
                        if (asyncContext == null || !asyncContext.isComplete()) {
                            return;
                        }

                        if (httpLogger.isDebugEnabled()) {
                            httpLogger.debug("{}", asyncContext.getResponse());
                        }
                        sendResponse(ctx, asyncContext.getResponse().httpResponse());
                    } catch (Exception e) {
                        logger.error("process error", e);
                    }
                });
            } catch (RejectedExecutionException re) {
                HttpCommand responseCommand = asyncContext.getRequest().createHttpCommandResponse(ProxyResponseCode.OVERLOAD.getRetCode(), ProxyResponseCode.OVERLOAD.getErrMsg());
                asyncContext.onComplete(responseCommand);
                try {
                    sendResponse(ctx, asyncContext.getResponse().httpResponse());
                } catch (Exception e) {
                }
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            super.channelReadComplete(ctx);
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            if (null != cause)
                cause.printStackTrace();
            if (null != ctx)
                ctx.close();
        }
    }
}
