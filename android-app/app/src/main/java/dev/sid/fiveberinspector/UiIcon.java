package dev.sid.fiveberinspector;
import android.graphics.*;import android.graphics.drawable.Drawable;
/** Small authored 24dp navigation icons; native 48dp buttons provide semantics. */
final class UiIcon extends Drawable {
 private final Paint paint=new Paint(3);private final String kind;
 UiIcon(String kind,int color){this.kind=kind;paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.8f);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeJoin(Paint.Join.ROUND);}
 public void draw(Canvas c){c.save();Rect b=getBounds();c.translate(b.left,b.top);c.scale(b.width()/24f,b.height()/24f);Path p=new Path();
 if(kind.equals("back")){p.moveTo(14,5);p.lineTo(7,12);p.lineTo(14,19);p.moveTo(7,12);p.lineTo(21,12);c.drawPath(p,paint);}
 else {float[] xy={9,3,15,3,16,6,19,7,21,12,19,17,16,18,15,21,9,21,8,18,5,17,3,12,5,7,8,6};p.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)p.lineTo(xy[i],xy[i+1]);p.close();c.drawPath(p,paint);c.drawCircle(12,12,3,paint);}c.restore();}
 public void setAlpha(int a){paint.setAlpha(a);}public void setColorFilter(ColorFilter f){paint.setColorFilter(f);}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
