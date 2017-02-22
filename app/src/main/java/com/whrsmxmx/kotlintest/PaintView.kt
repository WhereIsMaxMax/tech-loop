
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class PaintView : ViewGroup {

    constructor(context: Context): super(context){
        val circle: View
//        circle.background = Drawable.createFromXml(R.dravable.)
    }

    constructor(context: Context, attributeSet: AttributeSet): super (context, attributeSet){

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }
}