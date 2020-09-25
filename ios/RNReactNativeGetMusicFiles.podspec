
Pod::Spec.new do |s|
  s.name         = "RNReactNativeGetMusicFiles"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativeGetMusicFiles"
  s.description  = <<-DESC
                  RNReactNativeGetMusicFiles
                   DESC
  s.homepage = "https://github.com/cinder92/react-native-get-music-files"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNReactNativeGetMusicFiles.git", :tag => "master" }
  s.source_files  = "RNReactNativeGetMusicFiles/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  
