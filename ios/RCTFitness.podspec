
Pod::Spec.new do |s|
  s.name         = "RCTFitness"
  s.version      = "0.1.0"
  s.summary      = "RCTFitness"
  s.description  = <<-DESC
                  RCTFitness
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license    = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author       = { "Francesco Voto" => "fv@ovalmoney.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/OvalMoney/react-native-fitness", :tag => "master" }
  s.source_files = "RCTFitness/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

