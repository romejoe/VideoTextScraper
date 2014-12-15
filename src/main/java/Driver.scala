import org.jcodec.api.FrameGrab

/**
 * Created by Joey on 12/14/14.
 */
object Driver {

  def def main (args: Array[String]) {
    var grab:FrameGrab = new FrameGrab(new File("asdf"))

    grab.getFrame()
  }
}
