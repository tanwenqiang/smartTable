package com.bin.david.form.data.format.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.bin.david.form.data.Column;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.exception.TableException;

/**
 * Created by huang on 2017/10/30.
 */

public abstract class TextImageDrawFormat<T> extends ImageResDrawFormat<T> {

    public static final int LEFT =0;
    public static final int TOP =1;
    public static final int RIGHT =2;
    public static final int BOTTOM =3;

   private TextDrawFormat<T> textDrawFormat;
   private int drawPadding;
   private int direction;
   private Rect rect;

    public TextImageDrawFormat(int imageWidth, int imageHeight,int drawPadding) {
       this(imageWidth,imageHeight,LEFT,drawPadding);

    }

    public TextImageDrawFormat(int imageWidth, int imageHeight,int direction,int drawPadding) {
        super(imageWidth, imageHeight);
        textDrawFormat = new TextDrawFormat<>();
        this.rect = new Rect();
        this.direction = direction;
        this.drawPadding = drawPadding;
        if(direction >BOTTOM || direction <LEFT){
            throw  new TableException("Please set the direction less than 3 greater than 0");
        }

    }

    @Override
    public int measureWidth(Column<T>column, TableConfig config) {
        int textWidth = textDrawFormat.measureWidth(column, config);
        if(direction == LEFT || direction == RIGHT) {
            return getImageWidth() + textWidth+drawPadding;
        }else {
            return Math.max(super.measureWidth(column,config),textWidth);
        }
    }

    @Override
    public int measureHeight(Column<T> column,int position, TableConfig config) {
        int imgHeight = super.measureHeight(column,position,config);
        int textHeight = textDrawFormat.measureHeight(column,position,config);

        if(direction == TOP || direction == BOTTOM) {
            return getImageHeight() + textHeight+drawPadding;
        }else {
            return Math.max(imgHeight,textHeight);
        }
    }

    @Override
    public void draw(Canvas c, Column<T> column, T t, String value, Rect rect, int position, TableConfig config) {
        setDrawBg(true);
        cellInfo.set(column,t,value,position);
        drawBackground(c,cellInfo,rect,config);
        setDrawBg(false);
        textDrawFormat.setDrawBg(false);
        if(getBitmap(t,value,position) == null){
            textDrawFormat.draw(c,column,t,value,rect,position,config);
            return;
        }
        int imgWidth = (int) (getImageWidth()*config.getZoom());
        int imgHeight = (int) (getImageHeight()*config.getZoom());
        switch (direction){
            case LEFT:
                this.rect.set(rect.left+(imgWidth+drawPadding)/2,rect.top,rect.right,rect.bottom);
                textDrawFormat.draw(c,column,t,value,this.rect,position,config);
                int imgRight = (rect.right+rect.left)/2- textDrawFormat.measureWidth(column,config)/2 - drawPadding;
                this.rect.set(imgRight-imgWidth,rect.top,imgRight,rect.bottom);
                super.draw(c,column,t,value,this.rect,position,config);
                break;
            case RIGHT:
                this.rect.set(rect.left,rect.top,rect.right-(imgWidth+drawPadding)/2,rect.bottom);
                textDrawFormat.draw(c,column,t,value,rect,position,config);
                int imgLeft = (rect.right+rect.left)/2+ textDrawFormat.measureWidth(column,config)/2 + drawPadding;
                this.rect.set(imgLeft,rect.top,imgLeft+imgWidth,rect.bottom);
                super.draw(c,column,t,value,this.rect,position,config);
                break;
            case TOP:
                this.rect.set(rect.left,rect.top+(imgHeight+drawPadding)/2,rect.right,rect.bottom);
                textDrawFormat.draw(c,column,t,value,this.rect,position,config);
                int imgBottom = (rect.top+rect.bottom)/2- textDrawFormat.measureHeight(column,position,config)/2+drawPadding;
                this.rect.set(rect.left,imgBottom -imgHeight,rect.right,imgBottom);
                super.draw(c,column,t,value,this.rect,position,config);
                break;
            case BOTTOM:
                this.rect.set(rect.left,rect.top,rect.right,rect.bottom-(imgHeight+drawPadding)/2);
                textDrawFormat.draw(c,column,t,value,this.rect,position,config);
                int imgTop = (rect.top+rect.bottom)/2+ textDrawFormat.measureHeight(column,position,config)/2-drawPadding ;
                this.rect.set(rect.left,imgTop,rect.right,imgTop +imgHeight);
                super.draw(c,column,t,value, this.rect,position,config);
                break;

        }
    }
}
