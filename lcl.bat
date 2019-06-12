@echo off

rem 测试管理员权限，设置环境变量需要使用
:test_perm
rd "%windir%\system32\test_permissions" >nul 2>nul
md "%windir%\system32\test_permissions" 2>nul || (
  java.exe >nul 2>nul
  if errorlevel 9009 (
    echo 当前无管理员权限，如果要自动设置环境变量，请按下n后右键以管理员权限运行，否则按y
    choice
    if errorlevel 2 goto bad_end
    if errorlevel 1 goto install
  )
)
rd "%windir%\system32\test_permissions" 2>nul

rem 安装需要的 Java / 7z
:install
rem jre
java.exe >nul 2>nul
if errorlevel 9009 (
  echo 无可用 Java，即将下载
  echo 你需要同意 Java 的用户协议 www.oracle.com/downloads/licenses/javase-license1.html
  choice /c:yn /m:"按下y同意，按下n不同意"
    if errorlevel 2 goto bad_end
    if errorlevel 1 goto jredl
)
rem 7z
7z.exe >nul 2>nul
if errorlevel 9009 (
  echo 无可用 7z，即将下载
  goto 7zdl
)
start javaw -jar Lcl.jar
exit 0

:jredl
setlocal
echo 正在下载 Java 环境 ....
timeout /T 2
set url=https://arclight.oss-cn-zhangjiakou.aliyuncs.com/jre8.7z
set file=jre8.7z
bitsadmin /transfer jre /priority foreground /dynamic "%url%" "%CD%\%file%"
endlocal
goto unzip

:7zdl
setlocal
echo 正在下载 7z ...
timeout /T 2
set url=https://arclight.oss-cn-zhangjiakou.aliyuncs.com/7za.exe
set file=7z.exe
bitsadmin /transfer 7z /priority foreground /dynamic "%url%" "%CD%\%file%"
endlocal
goto install

:unzip
if not exist jre8 (
  7z x jre8.7z
)
echo 解压完成
setx /m path %path%;%CD%\jre8\bin
if errorlevel 1 (
  echo 没有管理员权限，当前环境变量为临时变量
  setx path %path%;%CD%\jre8\bin
)
java -version >nul 2>nul
if errorlevel 0 echo Java配置完成
goto install

:bad_end
echo 未完成配置启动器
timeout /T 5
exit /b 1
