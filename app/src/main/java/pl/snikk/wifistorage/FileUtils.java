package pl.snikk.wifistorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    public static boolean moveFile(String oldpath, String newpath) {
        File oldfile = new File(oldpath);
        File newfile = new File(newpath);
        return oldfile.renameTo(newfile);
    }

    public static boolean copyFile(String oldpath, String newpath) {
        File target = new File(oldpath);
        File destination = new File(newpath);
        if (target.isDirectory()) {
            if(!destination.mkdir())
                return false;
            for(String filename:target.list()) {
                copyFile(oldpath+"/"+filename, newpath+"/"+filename);
            }
        } else
            try {
                InputStream in = new FileInputStream(target);
                OutputStream out = new FileOutputStream(destination);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        return true;
    }

    public static boolean deleteFile(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFile(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String getFileType(File file) {
        if (file.isDirectory())
            return "Folder";
        String ext = file.getName().substring(file.getName().lastIndexOf(".")+1);
        if (extensions.containsKey(ext))
            return extensions.get(ext);
        else
            return "Unknown file";
    }

    private static final Map<String, String> extensions;

    static {
        extensions = new HashMap<String, String>();
        extensions.put("323","Internet Telephony");
        extensions.put("aac","AAC audio file");
        extensions.put("abw","AbiWord document");
        extensions.put("acx","Atari ST Executable");
        extensions.put("ai","Adobe Illustrator file");
        extensions.put("aif","AIFF audio file");
        extensions.put("aifc","AIFF audio file");
        extensions.put("aiff","AIFF audio file");
        extensions.put("asf","Windows Media file");
        extensions.put("asp","ASP source file");
        extensions.put("asr","Windows Media file");
        extensions.put("asx","Windows Media file");
        extensions.put("au","Audio file");
        extensions.put("avi","AVI video file");
        extensions.put("axs","ActiveX Script ");
        extensions.put("bas","BASIC Source Code ");
        extensions.put("bin","Binary File");
        extensions.put("bmp","Bitmap image");
        extensions.put("bz2","Compressed bzip2 file");
        extensions.put("c","C source file");
        extensions.put("c++","C++ source file");
        extensions.put("cab","Microsoft Cabinet archive");
        extensions.put("cat","Security Catalog ");
        extensions.put("cct","Adobe Director file");
        extensions.put("cdf","Channel Definition Format");
        extensions.put("cer","Internet Security Certificate file");
        extensions.put("cfc","ColdFusion source file");
        extensions.put("cfm","ColdFusion source file");
        extensions.put("class","Java bytecode file");
        extensions.put("clp","Windows Clipboard/Picture");
        extensions.put("cmx","Presentation Exchange Image ");
        extensions.put("cod","CIS-COD file");
        extensions.put("cp","C++ source file");
        extensions.put("cpio","UNIX CPIO Archive");
        extensions.put("cpp","C++ source file");
        extensions.put("crd","Windows Cardfile ");
        extensions.put("crt","Internet Security Certificate file");
        extensions.put("crl","Certificate Revocation List ");
        extensions.put("crt","Certificate File ");
        extensions.put("csh","C Shell file");
        extensions.put("css","Cascading stylesheet file");
        extensions.put("cst","Adobe Director file");
        extensions.put("csv","Comma-delimited file");
        extensions.put("cxt","Adobe Director file");
        extensions.put("dcr","Adobe Director file");
        extensions.put("der","Internet Security Certificate file");
        extensions.put("dib","Bitmap image");
        extensions.put("diff","Patch source file");
        extensions.put("dir","Adobe Director file");
        extensions.put("dll","Dynamic Link Library ");
        extensions.put("dms","DISKMASHER Compressed Archive");
        extensions.put("doc","Microsoft Word document");
        extensions.put("docm","Microsoft Word document");
        extensions.put("docx","Microsoft Word file");
        extensions.put("dot","Word Document Template ");
        extensions.put("dotm","Microsoft Word template file");
        extensions.put("dotx","Microsoft Word template file");
        extensions.put("dta","Stata Data file");
        extensions.put("dv","Digital video file");
        extensions.put("dvi","DVI file");
        extensions.put("dwg","AutoCAD drawing");
        extensions.put("dxf","AutoCAD drawing");
        extensions.put("dxr","Adobe Director file");
        extensions.put("elc","Emacs source file");
        extensions.put("eml","Web Archive file");
        extensions.put("enl","EndNote Library file");
        extensions.put("enz","EndNote Library file");
        extensions.put("eps","PostScript file");
        extensions.put("etx","Setext (Structure Enhanced Text)");
        extensions.put("evy","Envoy Document ");
        extensions.put("exe","Windows executable file");
        extensions.put("fif","Fractal Image Format");
        extensions.put("flac","Free Lossless Audio Codec file");
        extensions.put("flr","Virtual Reality Modeling Language file");
        extensions.put("fm","FrameMaker file");
        extensions.put("fqd","Adobe Director file");
        extensions.put("gif","GIF image");
        extensions.put("gtar","Compressed tar file");
        extensions.put("gz","Compressed gzip archive");
        extensions.put("h","C header file");
        extensions.put("hdf","Hierarchical Data Format file");
        extensions.put("hlp","Windows Help File");
        extensions.put("hqx","BinHex archive");
        extensions.put("hta","HTML application file");
        extensions.put("htc","HTML Component file");
        extensions.put("htm","HTML file");
        extensions.put("html","HTML file");
        extensions.put("htt","Hypertext Template File");
        extensions.put("ico","Favicon, Icon file");
        extensions.put("ics","Calendar file");
        extensions.put("ief","IEF Image File");
        extensions.put("iii","Intel IPhone Compatible File");
        extensions.put("indd","Adobe InDesign file");
        extensions.put("ins","IIS Internet Communications Settings file");
        extensions.put("isp","IIS Internet Service Provider Settings file");
        extensions.put("jad","Java Application Descriptor");
        extensions.put("jar","Java Archive file");
        extensions.put("java","Java source file");
        extensions.put("jfif","JPEG image");
        extensions.put("jpe","JPEG image");
        extensions.put("jpeg","JPEG image");
        extensions.put("jpg","JPEG image");
        extensions.put("js","JavaScript source file");
        extensions.put("kml","KML file");
        extensions.put("kmz","Compressed KML file");
        extensions.put("latex","LATEX file");
        extensions.put("lha","Compressed archive file");
        extensions.put("lib","EndNote Library file");
        extensions.put("llb","LabVIEW application file");
        extensions.put("log","Log text file");
        extensions.put("lsf","Streaming Audio/Video File");
        extensions.put("lsx","Streaming Audio/Video File");
        extensions.put("lvx","LabVIEW CAL Simulation file");
        extensions.put("lzh","Compressed archive file");
        extensions.put("m","Objective C source file");
        extensions.put("m1v","MPEG video file");
        extensions.put("m2v","MPEG video file");
        extensions.put("m3u","MP3 playlist file");
        extensions.put("m4a","MPEG-4 audio file");
        extensions.put("m4v","MPEG-4 video file");
        extensions.put("ma","Mathematica file");
        extensions.put("mail","Web Archive file");
        extensions.put("man","Troff with MAN macros file");
        extensions.put("mcd","MathCaD file");
        extensions.put("mdb","Microsoft Access file");
        extensions.put("me","Troff with ME macros file");
        extensions.put("mfp","Adobe Flash file");
        extensions.put("mht","Web Archive file");
        extensions.put("mhtml","Web Archive file");
        extensions.put("mid","MIDI audio file");
        extensions.put("midi","MIDI audio file");
        extensions.put("mif","FrameMaker file");
        extensions.put("mny","Money Data File");
        extensions.put("mov","Quicktime video file");
        extensions.put("mp2","MPEG layer 2 audio file");
        extensions.put("mp3","MP3 audio file");
        extensions.put("mp4","MPEG video file");
        extensions.put("mpa","MPEG Audio Stream");
        extensions.put("mpe","MPEG video file");
        extensions.put("mpeg","MPEG video file");
        extensions.put("mpg","MPEG video file");
        extensions.put("mpp","Microsoft Project file");
        extensions.put("mpv2","MPEG Audio Stream");
        extensions.put("mqv","Quicktime video file");
        extensions.put("ms","Troff with MS macros file");
        extensions.put("mvb","Multimedia Viewer");
        extensions.put("mws","Maple Worksheet file");
        extensions.put("nb","Mathematica file");
        extensions.put("nws","Web Archive file");
        extensions.put("oda","ODA Document");
        extensions.put("odf","OpenOffice formula file");
        extensions.put("odg","OpenOffice graphics file");
        extensions.put("odp","OpenOffice presentation file");
        extensions.put("ods","OpenOffice spreadsheet file");
        extensions.put("odt","OpenOffice document");
        extensions.put("ogg","OGG audio file");
        extensions.put("one","Microsoft OneNote file");
        extensions.put("p12","Internet Security Certificate file");
        extensions.put("patch","Patch source file");
        extensions.put("pbm","Portable bitmap image");
        extensions.put("pcd","Kodak Photo CD file");
        extensions.put("pct","Macintosh Quickdraw image");
        extensions.put("pdf","Adobe Acrobat file");
        extensions.put("pfx","Personal Information Exchange File");
        extensions.put("pgm","Portable graymap image");
        extensions.put("php","PHP source file");
        extensions.put("pic","Macintosh Quickdraw image");
        extensions.put("pict","Macintosh Quickdraw image");
        extensions.put("pjpeg","JPEG image");
        extensions.put("pl","Perl source file");
        extensions.put("pls","MP3 playlist file");
        extensions.put("pko","PublicKey Security Object ");
        extensions.put("pm","Perl source file");
        extensions.put("pmc","Windows Performance Monitor File ");
        extensions.put("png","PNG image");
        extensions.put("pnm","Portable Any Map Graphic Bitmap ");
        extensions.put("pod","Perl documentation file");
        extensions.put("potm","Microsoft PowerPoint template file");
        extensions.put("potx","Microsoft PowerPoint template file");
        extensions.put("ppam","Microsoft PowerPoint file");
        extensions.put("ppm","Portable pixmap image");
        extensions.put("pps","Microsoft PowerPoint file");
        extensions.put("ppsm","Microsoft PowerPoint file");
        extensions.put("ppsx","Microsoft PowerPoint file");
        extensions.put("ppt","Microsoft PowerPoint file");
        extensions.put("pptm","Microsoft PowerPoint file");
        extensions.put("pptx","Microsoft PowerPoint file");
        extensions.put("prf","PICS Rules file");
        extensions.put("ps","PostScript file");
        extensions.put("psd","Adobe Photoshop file");
        extensions.put("pub","Microsoft Publisher file");
        extensions.put("py","Python source file");
        extensions.put("qt","Quicktime video file");
        extensions.put("ra","RealAudio file");
        extensions.put("ram","RealAudio file");
        extensions.put("rar","Compressed archive");
        extensions.put("ras","Raster image");
        extensions.put("rgb","IRIS image");
        extensions.put("rm","RealMedia file");
        extensions.put("rmi","Radio MIDI File ");
        extensions.put("roff","Troff file");
        extensions.put("rpm","RealAudio file");
        extensions.put("rtf","Rich Text Format file");
        extensions.put("rtx","Rich Text Format file");
        extensions.put("rv","RealVideo file");
        extensions.put("sas","SAS file");
        extensions.put("sav","SPSS file");
        extensions.put("scd","Schedule Data");
        extensions.put("scm","Scheme file");
        extensions.put("sct","Windows Script Component");
        extensions.put("sd2","SPSS file");
        extensions.put("sea","Self-extracting archive");
        extensions.put("sh","Shell script file");
        extensions.put("shar","UNIX shar Archive File");
        extensions.put("shtml","HTML file");
        extensions.put("sit","Stuffit archive");
        extensions.put("smil","SMIL file");
        extensions.put("snd","Audio file");
        extensions.put("spl","Adobe Flash file");
        extensions.put("spo","SPSS file");
        extensions.put("sql","SQL file");
        extensions.put("src","WAIS source file");
        extensions.put("sst","Certificate Store Crypto Shell Extension");
        extensions.put("stl","Certificate Trust List");
        extensions.put("stm","SHTML File ");
        extensions.put("swa","Adobe Director file");
        extensions.put("swf","Adobe Flash file");
        extensions.put("sxw","OpenOffice.org document");
        extensions.put("t","Troff file");
        extensions.put("tar","Compressed tar file");
        extensions.put("tcl","TCL source file");
        extensions.put("tex","TeX file");
        extensions.put("tga","Truevision Targa image");
        extensions.put("tgz","Compressed gzip archive");
        extensions.put("tif","Tagged image file");
        extensions.put("tiff","Tagged image file");
        extensions.put("tnef","Microsoft Exchange TNEF file");
        extensions.put("tr","Troff file");
        extensions.put("trm","Terminal Settings");
        extensions.put("tsv","Tab-delimited file");
        extensions.put("twb","Tableau Workbook file");
        extensions.put("twbx","Tableau Workbook file");
        extensions.put("txt","Plain text file");
        extensions.put("uls","Internet Location Service");
        extensions.put("ustar","POSIX tar Compressed Archive");
        extensions.put("vcf","vCard File");
        extensions.put("vrml","Virtual Reality Modeling Language File");
        extensions.put("vsd","Microsoft Visio file");
        extensions.put("w3d","Adobe Director file");
        extensions.put("war","KDE Web archive");
        extensions.put("wav","Waveform audio file");
        extensions.put("wcm","Works File Transmission");
        extensions.put("wdb","Microsoft Works file");
        extensions.put("wks","Microsoft Works file");
        extensions.put("wma","Windows Media file");
        extensions.put("wmf","Windows Media file");
        extensions.put("wmv","Windows Media file");
        extensions.put("wmz","Windows Media Compressed file");
        extensions.put("wpd","WordPerfect document");
        extensions.put("wps","Microsoft Works file");
        extensions.put("wri","Write Document");
        extensions.put("wrl","VRML 3D file");
        extensions.put("wrz","VRML 3D file");
        extensions.put("xbm","Bitmap image");
        extensions.put("xhtml","HTML file");
        extensions.put("xla","Excel Add-in");
        extensions.put("xlam","Microsoft Excel file");
        extensions.put("xlc","Excel Chart ");
        extensions.put("xll","Microsoft Excel file");
        extensions.put("xlm","Excel Macro File");
        extensions.put("xls","Microsoft Excel file");
        extensions.put("xlsb","Microsoft Excel file");
        extensions.put("xlsm","Microsoft Excel file");
        extensions.put("xlsx","Microsoft Excel file");
        extensions.put("xlt","Excel Template File");
        extensions.put("xltm","Microsoft Excel file");
        extensions.put("xltx","Microsoft Excel template file");
        extensions.put("xlw","Excel Workspace File");
        extensions.put("xml","XML file");
        extensions.put("xpm","Pixmap image");
        extensions.put("xps","Microsoft XPS file");
        extensions.put("xsl","XSLT stylesheet file");
        extensions.put("xwd","X Windows Dump");
        extensions.put("z","UNIX Compressed Archive File");
        extensions.put("zip","Compressed zip archive");
    }
}

