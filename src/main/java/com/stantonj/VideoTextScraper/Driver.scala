package com.stantonj.VideoTextScraper

import java.awt.Rectangle
import java.io.File

import org.clapper.argot.{ArgotConversionException, ArgotParser, ArgotUsageException}

object Driver {

  val parser = new ArgotParser(
    "VideoTextScraper",
    preUsage = Some("VideoTextScraper: Version 0.1. Copyright (c) " +
      "2014, Joe Stanton.")
  )

  val TimeOffset = parser.option[Double](List("s", "StartTime"), "t", "Time to start Scraping") {
    (sValue, opt) =>

      try {
        require(sValue.toDouble >= 0.0)
        sValue.toDouble
      }

      catch {
        case _: NumberFormatException =>
          throw new ArgotConversionException(
            "Option " + opt.name + ": \"" + sValue + "\" isn't a valid start time."
          )
      }
  }

  val TimeStep = parser.option[Double](List("t", "TimeStep"), "t", "Size of time step to use") {
    (sValue, opt) =>

      try {
        require(sValue.toDouble > 0.0)
        sValue.toDouble
      }

      catch {
        case _: NumberFormatException =>
          throw new ArgotConversionException(
            "Option " + opt.name + ": \"" + sValue + "\" isn't a valid time step."
          )
      }
  }

  val input = parser.parameter[String]("inputfile",
    "Video file to scrape",
    false) {
    (s, opt) =>
      val file = new File(s)
      if (!file.exists)
        parser.usage("Input file \"" + s + "\" does not exist.")

      s
  }

  val areas = parser.multiParameter[Rectangle]("aoi",
    "Areas of interest to scrape (x,y,w,h)",
    false) {
    (s: String, opt) =>
      val _parts = s.replace("(", " ").replace(")", " ").replace(",", " ").split("[ \\t]+").toList //.map(_.toInt)
    val parts = _parts.filter(_ != "").map(_.toInt)
      new Rectangle(parts(0), parts(1), parts(2), parts(3))
  }

  def main (args: Array[String]) {
    try {
      parser.parse(args)
      val offset = TimeOffset.value.getOrElse(0.0)
      val step = TimeStep.value.getOrElse(1.0)
      val inputFilePath = input.value.get

      val Scraper = new VideoTextScraper(inputFilePath, areas.value.toList, step, offset)
      Scraper.persistFrames()
    }
    catch {
      case e: ArgotUsageException => println(e.message)
    }
  }
}
