package com.undcover.freedom.pyramid;

import android.content.Context;

import com.chaquo.python.PyObject;
import com.github.catvod.Proxy;
import com.github.catvod.utils.Util;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spider extends com.github.catvod.crawler.Spider {

    private final PyObject app;
    private final PyObject obj;
    private final Gson gson;

    public Spider(PyObject app, PyObject obj) {
        this.gson = new Gson();
        this.app = app;
        this.obj = obj;
    }

    @Override
    public void init(Context context) {
        app.callAttr("init", obj);
    }

    @Override
    public void init(Context context, String extend) {
        app.callAttr("init", obj, extend);
    }

    @Override
    public String homeContent(boolean filter) {
        return app.callAttr("homeContent", obj, filter).toString();
    }

    @Override
    public String homeVideoContent() {
        return app.callAttr("homeVideoContent", obj).toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        return app.callAttr("categoryContent", obj, tid, pg, filter, gson.toJson(extend)).toString();
    }

    @Override
    public String detailContent(List<String> ids) {
        return app.callAttr("detailContent", obj, gson.toJson(ids)).toString();
    }

    @Override
    public String searchContent(String key, boolean quick) {
        return app.callAttr("searchContent", obj, key, quick).toString();
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) {
        return app.callAttr("searchContentPage", obj, key, quick, pg).toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        return replaceProxy(app.callAttr("playerContent", obj, flag, id, gson.toJson(vipFlags)).toString());
    }

    @Override
    public boolean manualVideoCheck() {
        return app.callAttr("manualVideoCheck", obj).toBoolean();
    }

    @Override
    public boolean isVideoFormat(String url) {
        return app.callAttr("isVideoFormat", obj, url).toBoolean();
    }

    @Override
    public Object[] proxyLocal(Map<String, String> params) {
        List<PyObject> list = app.callAttr("localProxy", obj, gson.toJson(params)).asList();
        Map<PyObject, PyObject> headers = list.size() > 3 ? list.get(3).asMap() : null;
        boolean base64 = list.size() > 4 && list.get(4).toInt() == 1;
        PyObject r2 = list.get(2);
        Object[] result = new Object[4];
        result[0] = list.get(0).toInt();
        result[1] = list.get(1).toString();
        result[2] = r2 == null ? null : getStream(r2, base64);
        result[3] = headers;
        return result;
    }

    private ByteArrayInputStream getStream(PyObject o, boolean base64) {
        if (o.type().toString().contains("bytes")) {
            return new ByteArrayInputStream(o.toJava(byte[].class));
        } else {
            String content = replaceProxy(o.toString());
            if (base64 && content.contains("base64,")) content = content.split("base64,")[1];
            return new ByteArrayInputStream(base64 ? Util.decode(content) : content.getBytes());
        }
    }

    private String replaceProxy(String content) {
        return content.replace("http://127.0.0.1:UndCover/proxy", Proxy.getUrl(true));
    }
}
