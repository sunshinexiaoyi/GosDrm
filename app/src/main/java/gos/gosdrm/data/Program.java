package gos.gosdrm.data;

/*
*节目信息类
* Created by wuxy on 2017/9/22.
*/

/*
    {    "outputFilePath":"/home/content/vod",  //输出流路径

         "status":2,       //状态 1 加密中 2 加密
         "inputFilePath":"/home/tengym/share-http/movies/langyb.mp4",//输入流路径
         "inputStreamPort":null,
         "contentId":"6WAPYhai",    //资源id
         "id":157,                  //id
         "outputStreamPort":null,

         "transmissionMode":1,   //传输模式 1 点播 2直播
         "inputStreamMode":"1",  //输入流模式 1 file
         "outputStreamMode":"1"  //输出流模式 1 file

         "streamSystemType":"1",
         "progressBar":100,
         "outputStreamIP":null,
         "fileName":"test_4",   //文件名称
         "inputStreamIP":null,
         "encryptorName":"gospell",//加密机名称
         "encryptSystemType":"2"  // 加密机类型
         "encryptMode":"1", //加密模式
   }
*/

public class Program {
    private int id;              //id
    private String contentId;    //资源id
    private String fileName;     //文件名称

    private int status;          //状态 1 加密中 2 加密
    private String encryptorName;//加密机名称
    private String encryptSystemType;// 加密机类型   1chinadrm 2widevine 3playready 4gosdrm
    //private int encryptStrength; // value:1 ~ 100(1% ~ 100%). example 10:10%, 50:50%, 70:70%, 100:100%

    public Program() {
    }

    public Program(int id, String contentId, String fileName) {
        this.id = id;
        this.contentId = contentId;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEncryptorName() {
        return encryptorName;
    }

    public void setEncryptorName(String encryptorName) {
        this.encryptorName = encryptorName;
    }

    public String getEncryptSystemType() {
        return encryptSystemType;
    }

    public void setEncryptSystemType(String encryptSystemType) {
        this.encryptSystemType = encryptSystemType;
    }
}





