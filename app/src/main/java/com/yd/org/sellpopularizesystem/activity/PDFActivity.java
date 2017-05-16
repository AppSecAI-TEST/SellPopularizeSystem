package com.yd.org.sellpopularizesystem.activity;

import android.graphics.Canvas;
import android.widget.Toast;

import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;
import com.yd.org.sellpopularizesystem.R;
import com.yd.org.sellpopularizesystem.application.Contants;
import com.yd.org.sellpopularizesystem.javaBean.FileContent;
import com.yd.org.sellpopularizesystem.utils.ToasShow;

public class PDFActivity extends BaseActivity implements OnPageChangeListener
        , OnLoadCompleteListener, OnDrawListener {
    private PDFView pdfView;
    private FileContent fileContent;


    @Override
    protected int setContentView() {
        return R.layout.activity_pdf;
    }

    @Override
    public void initView() {
        hideRightImagview();
        showDialog();
        fileContent = (FileContent) getIntent().getSerializableExtra("pdf");
        setTitle(fileContent.getDetail_name());

        pdfView = (PDFView) findViewById(R.id.pdfView);
        displayFromFile1(Contants.DOMAIN + "/" + fileContent.getUrl(), fileContent.getDetail_name() + ".pdf");


    }

    @Override
    public void setListener() {

    }

    /**
     * 获取打开网络的pdf文件
     *
     * @param fileUrl
     * @param fileName
     */
    private void displayFromFile1(String fileUrl, String fileName) {

        pdfView.fileFromLocalStorage(this, this, this, fileUrl, fileName);   //设置pdf文件地址

    }

    /**
     * 翻页回调
     *
     * @param page
     * @param pageCount
     */
    @Override
    public void onPageChanged(int page, int pageCount) {
        ToasShow.showToastBottom(PDFActivity.this, page +
                "/" + pageCount);
    }

    /**
     * 加载完成回调
     *
     * @param nbPages 总共的页数
     */
    @Override
    public void loadComplete(int nbPages) {
        closeDialog();
        Toast.makeText(PDFActivity.this, "加载完成" + nbPages, Toast.LENGTH_SHORT).show();

        ToasShow.showToastBottom(PDFActivity.this, "加载完成" + nbPages);
    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        // Toast.makeText( MainActivity.this ,  "pageWidth= " + pageWidth + "
        // pageHeight= " + pageHeight + " displayedPage="  + displayedPage , Toast.LENGTH_SHORT).show();
    }

}
