package org.apache.rocketmq.proxy.common.protocol.http.header;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.proxy.common.ProtocolKey;
import org.apache.rocketmq.proxy.common.ProtocolVersion;
import org.apache.rocketmq.proxy.common.ProxyConstants;

public class SendMessageRequestHeader extends Header {
    private String code;
    private String language;
    private ProtocolVersion version;
    private String region;
    private String groupName;
    private String pid;
    private String ip;

    //prepare for acl
    private String username;
    private String passwd;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ProtocolVersion getVersion() {
        return version;
    }

    public void setVersion(ProtocolVersion version) {
        this.version = version;
    }

//    public String getEnv() {
//        return env;
//    }
//
//    public void setEnv(String env) {
//        this.env = env;
//    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

//    public String getIdc() {
//        return idc;
//    }
//
//    public void setIdc(String idc) {
//        this.idc = idc;
//    }
//
//    public String getDcn() {
//        return dcn;
//    }
//
//    public void setDcn(String dcn) {
//        this.dcn = dcn;
//    }
//
//    public String getSys() {
//        return sys;
//    }
//
//    public void setSys(String sys) {
//        this.sys = sys;
//    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static SendMessageRequestHeader buildHeader(Map<String, Object> headerParam) {
        SendMessageRequestHeader header = new SendMessageRequestHeader();
        header.setCode(MapUtils.getString(headerParam, ProtocolKey.REQUEST_CODE));
        header.setVersion(ProtocolVersion.get(MapUtils.getString(headerParam, ProtocolKey.VERSION)));
        String lan = StringUtils.isBlank(MapUtils.getString(headerParam, ProtocolKey.LANGUAGE))
            ? ProxyConstants.Language.JAVA : MapUtils.getString(headerParam, ProtocolKey.LANGUAGE);
        header.setLanguage(lan);
//        header.setEnv(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.ENV));
        header.setRegion(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.REGION));
//        header.setIdc(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.IDC));
//        header.setDcn(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.DCN));
//        header.setSys(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.SYS));
        header.setGroupName(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.GROUPNAME));
        header.setPid(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.PID));
        header.setIp(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.IP));
        header.setUsername(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.USERNAME));
        header.setPasswd(MapUtils.getString(headerParam, ProtocolKey.ClientInstanceKey.PASSWD));
        return header;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ProtocolKey.REQUEST_CODE, code);
        map.put(ProtocolKey.LANGUAGE, language);
        map.put(ProtocolKey.VERSION, version);
//        map.put(ProtocolKey.ClientInstanceKey.ENV, env);
        map.put(ProtocolKey.ClientInstanceKey.REGION, region);
//        map.put(ProtocolKey.ClientInstanceKey.IDC, idc);
//        map.put(ProtocolKey.ClientInstanceKey.DCN, dcn);
//        map.put(ProtocolKey.ClientInstanceKey.SYS, sys);
        map.put(ProtocolKey.ClientInstanceKey.GROUPNAME, groupName);
        map.put(ProtocolKey.ClientInstanceKey.PID, pid);
        map.put(ProtocolKey.ClientInstanceKey.IP, ip);
        map.put(ProtocolKey.ClientInstanceKey.USERNAME, username);
        map.put(ProtocolKey.ClientInstanceKey.PASSWD, passwd);
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("sendMessageRequestHeader={")
            .append("code=").append(code).append(",")
            .append("language=").append(language).append(",")
            .append("version=").append(version).append(",")
//            .append("env=").append(env).append(",")
            .append("region=").append(region).append(",")
//            .append("idc=").append(idc).append(",")
//            .append("dcn=").append(dcn).append(",")
//            .append("sys=").append(sys).append(",")
            .append("groupName=").append(groupName).append(",")
            .append("pid=").append(pid).append(",")
            .append("ip=").append(ip).append(",")
            .append("username=").append(username).append(",")
            .append("passwd=").append(passwd).append("}");
        return sb.toString();
    }
}
