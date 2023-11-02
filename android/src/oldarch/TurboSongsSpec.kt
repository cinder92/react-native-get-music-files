package com.turbosongs

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

abstract class TurboSongsSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {

  abstract fun getAll(options: ReadableMap, promise: Promise)
  abstract fun getAlbums(options: ReadableMap, promise: Promise)
  abstract fun search(options: ReadableMap, promise: Promise)
}
