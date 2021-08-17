Pod::Spec.new do |s|
    s.name             = 'DoricFs'
    s.version          = '0.2.1'
    s.summary          = 'Doric extension for FS'
  
  
    s.description      = <<-DESC
    Support file system api in Doric.
                         DESC
  
    s.homepage         = 'https://github.com/doric-pub'
    s.license          = { :type => 'Apache-2.0', :file => 'LICENSE' }
    s.author           = { 'pengfeizhou' => 'pengfeizhou@foxmail.com' }
    s.source           = { :git => 'https://github.com/doric-pub/DoricFs.git', :tag => s.version.to_s }
  
    s.ios.deployment_target = '9.0'
  
    s.source_files = 'iOS/Classes/**/*'
    s.resource     =  "dist/**/*"
    s.public_header_files = 'iOS/Classes/**/*.h'
    s.dependency 'DoricCore'
end
