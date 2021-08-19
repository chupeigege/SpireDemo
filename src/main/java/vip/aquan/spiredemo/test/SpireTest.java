package vip.aquan.spiredemo.test;

import com.spire.barcode.BarcodeScanner;
import com.spire.pdf.PdfDocument;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * 教程链接：https://www.e-iceblue.cn/spirebarcodejava/scan-barcode-for-java-2.html
 *
 * 产品特点：
 * 1.免费版本有些条形码不支持  2.可以申请正式版试用期一个月
 * 3.付费版本如果需要发布云服务，在购买第二款授权产品，5w永久使用，一年内的版本订阅，链接：http://www.e-iceblue.cn/Buy/Spire-Office-JAVA.html
 * 4.性能：自测平均时间2s左右
 * 5.付款要先拟定合同，然后走付款流程，公对公银行转账的方式付款
 */
public class SpireTest {
    static {
        //注册许可证密钥(支持更多的条形码类型)，可以找客服免费申请试用一个月
//        LicenseProvider.setLicenseKey("your license key");
    }

    public static void main(String[] args) {
        String[] result = new SpireTest().barcodeScanToImage();
//        String[] result = new SpireTest().barcodeScanToPdf();
        System.out.println("result:"+Arrays.toString(result));
    }

    /**
     * 基于图片扫描
     * @return
     */
    public String[] barcodeScanToImage(){

        long start = System.currentTimeMillis();

        //若找不到文件，报空指针，maven install即可
        InputStream inputStream = this.getClass().getResourceAsStream("/barcode.png");

        String[] data = null;
        try {

//            data = BarcodeScanner.scan(fileName , BarCodeType.Code_128);  //从图片中识别Code_128类型条形码
            data = BarcodeScanner.scan(inputStream);
        } catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("总耗时：" +(System.currentTimeMillis() - start) );

        return data;
    }

    /**
     * 基于pdf文件扫描(先把pdf转为图片，再扫描)，并利用Future做超时处理
     * @return
     */
    public String[] barcodeScanToPdf(){

        long start = System.currentTimeMillis();

        final ExecutorService exec = Executors.newSingleThreadExecutor();

        // Future是一个接口，该接口用来返回异步的结果
        Future<String[]> future = exec.submit(()->{
            //Pdf file
            //若找不到文件，报空指针，maven install即可
            InputStream inputStream = this.getClass().getResourceAsStream("/barcode.pdf");

            //Open pdf document
            PdfDocument pdf = new PdfDocument();
//        pdf.loadFromFile(path);
            pdf.loadFromStream(inputStream);

            //Convert a particular page to png
            //Set page index and image name
            int pageIndex = 0;

            //Save page to image
            BufferedImage image = pdf.saveAsImage(pageIndex);

            //输出图片
//                File fileOut = new File( "C:\\pdfToImage.png" );
//                ImageIO.write(image, "PNG", fileOut);

            pdf.close();
            return BarcodeScanner.scan(image);
        });

        String[] result = null;
        try{
            // 获取结果，并且设置超时时间10s
            result = future.get(10, TimeUnit.SECONDS);
        } catch(Exception e){
            e.printStackTrace();
        }
        //执行结束后，手动关闭线程池
        exec.shutdown();
        System.out.println("总耗时："+(System.currentTimeMillis() - start));

        return result;

    }
}
