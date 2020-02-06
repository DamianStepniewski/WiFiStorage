package pl.snikk.wifistorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.snikk.wifistorage.NanoHTTPD.Response.Status;

public class Server extends NanoHTTPD {

    private static final String[] scriptFiles = {"jquery", "login", "filelist.js", "scrollbar", "mousewheel", "scrollbarstyle"};
    private static final String styleFile = "style.css";
    private Context context;
    private Map<String,String> scripts;
    private String style;
    private String sessionId;
    private String password;

    Server(int port, Context context, String password){
        super(port);
        this.context = context;
        this.password = password;
        initialize();
    }

    private void initialize(){
        AssetManager assetManager = context.getAssets();
        try {
            scripts = new HashMap<String,String>();
            for (int i=0; i<scriptFiles.length;i++){
                InputStream input = assetManager.open(scriptFiles[i]);
                Scanner s = new Scanner(input);
                s.useDelimiter("\\A");
                scripts.put(scriptFiles[i],s.hasNext() ? s.next() : "");
                s.close();
                input.close();
            }

            InputStream input = assetManager.open(styleFile);
            Scanner s = new Scanner(input);
            s.useDelimiter("\\A");
            style = s.hasNext() ? s.next() : "";
            s.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parameters, Map<String, String> files) {
        String answer = "";
        try {
            JSONObject json = new JSONObject();
            if (parameters.containsKey("do")) {
                if (parameters.get("do").equals("login")) {
                    answer = authenticate(parameters.get("pass"));
                } else
                if (parameters.get("do").equals("dir")) {
                    if (parameters.get("ssid").equals(sessionId)) {
                        json.put("status", true);
                        json.put("html", getFileListPage(parameters.get("path")));
                    } else
                        json.put("status", false);
                    answer = json.toString();
                } else
                if (parameters.get("do").equals("delete")) {
                    if (parameters.get("ssid").equals(sessionId)) {
                        json.put("status", true);
                        JSONArray data = new JSONArray(parameters.get("paths"));
                        for (int i=0; i<data.length();i++) {
                            File file = new File(data.getJSONObject(i).getString("path"));
                            FileUtils.deleteFile(file);
                        }
                        json.put("html", getFileListPage(StringEscapeUtils.escapeHtml4(parameters.get("path"))));
                    } else
                        json.put("status", false);
                    answer = json.toString();
                } else
                if (parameters.get("do").equals("rename")) {
                    if (parameters.get("ssid").equals(sessionId)) {
                        json.put("status", true);
                        String oldName = parameters.get("filePath");
                        String newName = oldName.substring(0,oldName.lastIndexOf("/")+1)+parameters.get("newName");
                        FileUtils.moveFile(oldName, newName);
                        json.put("html", getFileListPage(StringEscapeUtils.escapeHtml4(parameters.get("path"))));
                    } else
                        json.put("status", false);
                    answer = json.toString();
                } else
                if (parameters.get("do").equals("newfolder")) {
                    if (parameters.get("ssid").equals(sessionId)) {
                        json.put("status", true);
                        File dir = new File(StringEscapeUtils.escapeHtml4(parameters.get("path"))+"/"+StringEscapeUtils.escapeHtml4(parameters.get("name")));
                        dir.mkdir();
                        json.put("html", getFileListPage(StringEscapeUtils.escapeHtml4(parameters.get("path"))));
                    } else
                        json.put("status", false);
                    answer = json.toString();
                } else
                if (parameters.get("do").equals("move") || parameters.get("do").equals("copy")) {
                    if (parameters.get("ssid").equals(sessionId)) {
                        json.put("status", true);
                        JSONArray data = new JSONArray(parameters.get("paths"));
                        String path = StringEscapeUtils.escapeHtml4(parameters.get("path"));
                        for (int i=0; i<data.length();i++) {
                            String oldPath = data.getJSONObject(i).getString("path");
                            String newPath = path+oldPath.substring(oldPath.lastIndexOf("/"));
                            if (parameters.get("do").equals("move"))
                                FileUtils.moveFile(oldPath, newPath);
                            else
                                FileUtils.copyFile(oldPath, newPath);
                        }
                        json.put("html", getFileListPage(path));
                    } else
                        json.put("status", false);
                    answer = json.toString();
                } else
                if (parameters.get("do").equals("dlfile")) {
                    if (parameters.get("ssid").equals(sessionId))
                        try {
                            FileInputStream fis = new FileInputStream(StringEscapeUtils.escapeHtml4(parameters.get("path")));
                            File file = new File(StringEscapeUtils.escapeHtml4(parameters.get("path")));
                            Response res = new Response(Status.OK, "application/octet-stream", fis);
                            res.addHeader("Content-Disposition","attachment; filename="+file.getName());
                            json.put("a","b");
                            return res;
                        } catch (FileNotFoundException e) {
                            json.put("status", e.getMessage());
                        }
                    else
                        json.put("status", false);
                    return new Response(json.toString());
                }
                else
                    answer = buildFullPage(getLoginPage());
            } else {
                answer = buildFullPage(getLoginPage());
            }
    	/*if (method.equals(Method.POST) && !files.isEmpty()) {
    		for (Entry<String, String> file:files.entrySet()) {
    			try {
					FileUtils.copyFile(StringEscapeUtils.escapeHtml4(file.getValue()), URLDecoder.decode(StringEscapeUtils.escapeHtml4(file.getKey()), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
				}
    		}
    	}*/
        } catch (JSONException e) {
            Log.d("JSON exception",e.getMessage());
        }
        return new NanoHTTPD.Response(answer);
    }

    private String getLoginPage() {
        String template = "<div id=\"password_box\">" +
                "<input type=\"password\" id=\"password\" autofocus placeholder=\"PASSWORD\"/>" +
                "<span id=\"password_incorrect\">Incorrect password</span>" +
                "<div id=\"button_login\"><b>LOG IN</b>" +
                "<div id=\"spinner\" class=\"spinner\" style=\"display:none\"></div>" +
                "</div>" +
                "<script>" +
                scripts.get("login") +
                "</script>";
        return template;
    }

    private String getFileListPage(String path) {
        File mainDir;
        if (path.length() == 0)
            mainDir = Environment.getExternalStorageDirectory();
        else
            mainDir = new File(path);
        File[] fileList = mainDir.listFiles();
        Arrays.sort(fileList, new FileSorter());

        ArrayList<File> aFiles = new ArrayList<File>();
        ArrayList<File> aDirectories = new ArrayList<File>();

        for (int i=0; i<fileList.length;i++) {
            if (fileList[i].isFile())
                aFiles.add(fileList[i]);
            else
                aDirectories.add(fileList[i]);
        }

        ArrayList<File> aEntities = new ArrayList<File>();
        aEntities.addAll(aDirectories);
        aEntities.addAll(aFiles);

        String answer = "<div id=\"topbar\"><div id=\"path\">"+mainDir.getAbsolutePath()+"</div></div>"+
                "<div id=\"leftcolumn\">"+
                "<ul id=\"tree\">"+
                "<li><div id=\"back\" path=\""+mainDir.getParent()+"\" class=\"pointer\"></div><p class=\"caption\">"+mainDir.getName()+"</p></li>";

        for (File f:aDirectories) {
            answer += "<li class=\"pointer treeitem\" path=\""+f.getAbsolutePath()+"\"><p class=\"caption\">"+f.getName()+"</p></li>";
        }

        answer += "</ul></div>"+
                "<div id=\"contentcolumn\"><div class=\"content\">";

        for (File f:aEntities) {
            Date date = new Date(f.lastModified());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", context.getResources().getConfiguration().locale);
            answer += "<div class=\"bigicon pointer\" name=\""+f.getName()+"\" path=\""+f.getAbsolutePath()+"\" size=\""+getFileSize(f)+"\" lastmodified=\""+format.format(date)+"\" type=\""+FileUtils.getFileType(f)+"\">";
            answer += f.isDirectory() ? "<div class=\"folder\"></div>" : "<div class=\"file\"></div>";
            answer += f.getName();
            answer += "</div>";
        }

        answer += "</div></div><div id=\"rightcolumn\"><div class=\"content\"><div id=\"filename\">-</div><br />" +
                "<b>Size:</b> <div id=\"filesize\">-</div><br />"+
                "<b>Last modified:</b> <div id=\"lastmodified\">-</div><br />" +
                "<b>Type:</b> <div id=\"filetype\">-</div>" +
                "</div></div>";

        answer += "<script>var ssid = \""+sessionId+"\";</script>";
        answer += "<script>"+scripts.get("filelist.js")+"</script>";

        return answer;
    }

    private String buildFullPage(String body) {
        return "<!DOCTYPE html><html>" +
                "<head><title>Wifi Storage</title>" +
                "<style>"+
                style+Embedded.IMAGE_SPINNER+Embedded.IMAGE_FILE+Embedded.IMAGE_FOLDER+Embedded.IMAGE_BACK+scripts.get("scrollbarstyle")+
                "</style>" +
                "</head>" +
                "<body>" +
                "<script>"+scripts.get("jquery")+"</script>" +
                "<script>"+scripts.get("scrollbar")+"</script>" +
                "<script>"+scripts.get("mousewheel")+"</script>" +
                "<script>var cut = []; var copied = []; var upload = {target: [], files: []}; var minimized = false;</script>" +
                "<div id=\"master\"><div id=\"wrapper\">"+
                body+
                "</div></div>" +
                "<div id=\"overlay\">" +
                "<div id=\"popup\"></div></div>" +
                "<div id=\"contextmenu\"></div>" +
                "<div id=\"uploadmanager_wrapper\"><div id=\"uploadmanager_title\"><span>Uploading</span><div id=\"uploadmanager_title_close\">x</div><div id=\"uploadmanager_title_minimize\"><div></div></div></div><div id=\"uploadmanager\"></div></div>" +
                "</body>" +
                "</html>";
    }

    private String authenticate(String pass) {
        JSONObject json = new JSONObject();
        try {
            if (pass.equals("abc")) {
                json.put("status", true);
                sessionId = generateSessionId();
                json.put("ssid", sessionId);
            }
            else {
                json.put("status", false);
            }
        } catch (JSONException e) {
        }
        return json.toString();
    }

    private String generateSessionId() {
        Random rnd = new Random();
        long longId = rnd.nextLong();
        return Long.toHexString(longId);
    }

    private long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory())
            for (File subFile:file.listFiles())
                size += getFileSize(subFile);
        else
            size += file.length();
        return size;
    }

    private class FileSorter implements Comparator<File> {
        public int compare(File o1, File o2) {
            String s1 =  o1.getName();
            String s2 =  o2.getName();
            return s1.toLowerCase(context.getResources().getConfiguration().locale).compareTo(s2.toLowerCase(context.getResources().getConfiguration().locale));
        }
    }
}
