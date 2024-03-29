require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
    s.name           = "react-native-agora-edu"
    s.version        = package["version"]
    s.summary        = package["description"]
    s.homepage       = package['homepage']
    s.license        = package['license']
    s.authors        = package["authors"]
    s.platform       = :ios, "8.0"

    s.source         = { :git => package["repository"]["url"] }
    s.source_files   = 'ios/RCTAgora/*.{h,m}'

    s.dependency 'React'
    s.dependency "AgoraRtcEngine_iOS", "2.9.1"
end
