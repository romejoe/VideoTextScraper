package com.stantonj.VideoTextScraper

import java.awt.image.BufferedImage
import java.awt.{Rectangle, Shape}
import java.io.{File, FileInputStream}
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.imageio.ImageIO

import org.jcodec.api.{FrameGrab, JCodecException}
import org.jcodec.common.ByteBufferSeekableByteChannel

import scala.collection.mutable

class VideoTextScraper(VideoFilePath: String, AOI: List[Shape], TimeStep: Double = 1.0, TimeOffset: Double = 0.0) {

  val (frames: mutable.HashMap[Double, List[BufferedImage]]) = {
    val videoInputStream = new FileInputStream(VideoFilePath)
    val videoFileChannel = videoInputStream.getChannel()
    val videoByteBuffer: MappedByteBuffer = videoFileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, videoFileChannel.size())
    var grab: FrameGrab = new FrameGrab(new ByteBufferSeekableByteChannel(videoByteBuffer))
    val frames = new mutable.HashMap[Double, List[BufferedImage]]()
    var TimeStamp: Double = TimeOffset

    val Areas: List[Rectangle] = AOI.map(_.getBounds())

    grab = grab.seekToSecondPrecise(TimeStamp)
    var done = false
    while (!done) {
      val frame: BufferedImage = grab.getFrame()

      frames.put(TimeStamp, Areas.map(a => frame.getSubimage(a.getX().toInt, a.getY().toInt, a.getWidth().toInt, a.getHeight().toInt)))
      //println(TimeStamp)
      TimeStamp += TimeStep
      //println(TimeStamp)
      try {
        grab = grab.seekToSecondSloppy(TimeStamp)
      }
      catch {
        case je: JCodecException => done = true
        case e: Exception => System.exit(-1)
      }
      //grab.seek(TimeStamp)//grab = grab.seek(TimeStamp)
    }

    (frames)
  }

  def persistFrames(): Unit = frames.foreach(p => p._2.zipWithIndex.foreach(a => ImageIO.write(a._1, "png", new File(VideoFilePath + "_" + p._1.toString() + "_" + a._2.toString() + ".png"))))

}
