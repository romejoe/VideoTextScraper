package com.stantonj.VideoTextScraper

import java.awt.Polygon
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

import org.jcodec.api.FrameGrab
import org.jcodec.common.ByteBufferSeekableByteChannel

class VideoTextScraper(VideoFilePath:String, AOI:List[Polygon]) {
  val videoInputStream = new FileInputStream(VideoFilePath)
  val videoFileChannel = videoInputStream.getChannel()
  val videoByteBuffer:MappedByteBuffer = videoFileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, videoFileChannel.size())
  var grab:FrameGrab = new FrameGrab( new ByteBufferSeekableByteChannel(videoByteBuffer))

}
