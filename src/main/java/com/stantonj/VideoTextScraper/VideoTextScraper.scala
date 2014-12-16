package com.stantonj.VideoTextScraper

import java.awt.image.BufferedImage
import java.awt.{Polygon, Rectangle}
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util

import org.jcodec.api.FrameGrab
import org.jcodec.common.ByteBufferSeekableByteChannel

class VideoTextScraper(VideoFilePath: String, AOI: List[Polygon], TimeStep: Double = 1.0, TimeOffset: Double = 0.0) {

  val (frames) = {
    val videoInputStream = new FileInputStream(VideoFilePath)
    val videoFileChannel = videoInputStream.getChannel()
    val videoByteBuffer: MappedByteBuffer = videoFileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, videoFileChannel.size())
    var grab: FrameGrab = new FrameGrab(new ByteBufferSeekableByteChannel(videoByteBuffer))
    val frames = new util.HashMap[Double, List[BufferedImage]]()
    var TimeStamp: Double = TimeOffset

    val Areas: List[Rectangle] = AOI.map(_.getBounds())

    grab = grab.seek(TimeStamp)
    while (grab != null) {
      val frame: BufferedImage = grab.getFrame()

      frames.put(TimeStamp, Areas.map(a => frame.getSubimage(a.getX().toInt, a.getY().toInt, a.getWidth().toInt, a.getHeight().toInt)))

      TimeStamp += TimeStep
      grab = grab.seek(TimeStamp)
    }

    (frames)
  }


}
