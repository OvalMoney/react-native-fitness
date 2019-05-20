require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = package['name']
  s.version      = package['version']
  s.summary      = package['description']

  s.authors      = package['contributors'].flat_map { |author| { author['name'] => author['email'] } }
  s.homepage     = package['homepage']
  s.license      = package['license']
  s.platform     = :ios, "7.0"

  s.source       = { :git => "https://github.com/OvalMoney/react-native-fitness", :tag => "master" }
  s.source_files  = "ios/RCTFitness/**/*.{h,m}"

  s.dependency 'React'
end