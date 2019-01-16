# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
-keep class com.shenmi.calculator.bean.** { *; }
-keep class com.sm.readbook.bean.** { *; }
#-------------------------------------------------------------------------
#---------------------------------2.第三方包-------------------------------
#---------------------------------腾讯bugly-------------------------------
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}
#--------------------------------友盟统计-------------------------------
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#--------------------------------友盟推送-------------------------------
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*
-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
-keep public class **.R$*{
   public static final int *;
}
#------------------------------无视第三方jar的混淆，只混淆自己的代码-----------------------------------
-dontwarn com.umeng.**
-dontwarn pub.devrel.easypermissions.**
-keep class com.umeng.** { *;}
-keep class pub.devrel.easypermissions.** { *;}
#-------------------------------------------------------------------------
#---------------------------------3.与js互相调用的类------------------------
#-------------------------------------------------------------------------
#---------------------------------4.反射相关的类和方法-----------------------
-keep public class com.shenmi.calculator.R$*{
public static final int *;
}
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------
#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses -keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep public class * extends android.view.View{
  *** get*();
  void set*(***);
  public <init>(android.content.Context);
  public <init>(android.content.Context, android.util.AttributeSet);
  public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
  public <init>(android.content.Context, android.util.AttributeSet);
  public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements java.io.Serializable {
  static final long serialVersionUID;
  private static final java.io.ObjectStreamField[] serialPersistentFields;
  private void writeObject(java.io.ObjectOutputStream);
  private void readObject(java.io.ObjectInputStream);
  java.lang.Object writeReplace(); java.lang.Object readResolve();
}
  -keep class **.R$* { *; }
  -keepclassmembers class * {
  void *(**On*Event);
}
#----------------------------------------------------------------------------
#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
   public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
   public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
   public void *(android.webkit.WebView, jav.lang.String);
}
#---------------------------------------------------------------------------- #---------------------------------------------------------------------------------------------------

#讯飞SDL混淆
-keep class com.iflytek.** {*;}
-keepattributes Signature


#retrofit2 混淆
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
# OkHttp3
-dontwarn okhttp3.logging.**
-keep class okhttp3.internal.**{*;}
-dontwarn okio.**
# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Retrofit
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8

# RxJava RxAndroid

-keepattributes EnclosingMethod

-keep class com.shenmi.calculator.bean.** { *; }
-keep class com.shenmi.calculator.net.** { *; }




#=======================================================================================
#shrink，测试后发现会将一些无效代码给移除，即没有被显示调用的代码，该选项 表示 不启用 shrink。
-dontshrink
#指定重新打包,所有包重命名,这个选项会进一步模糊包名，将包里的类混淆成n个再重新打包到一个个的package中
-flattenpackagehierarchy
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#混淆前后的映射
-printmapping map.txt
#不跳过(混淆) jars中的 非public classes   默认选项
-dontskipnonpubliclibraryclassmembers
#忽略警告
-ignorewarnings
#指定代码的压缩级别
-optimizationpasses 5
#不使用大小写混合类名
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不启用优化  不优化输入的类文件
-dontoptimize
#不预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-renamesourcefileattribute SourceFile
#保持源文件和行号的信息,用于混淆后定位错误位置
-keepattributes SourceFile,LineNumberTable
-keepattributes SourceFile,LineNumberTable
#保持含有Annotation字符串的 attributes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions,InnerClasses

-dontwarn org.apache.**
-dontwarn android.support.**

#基础配置
# 保持哪些类不被混淆
# 系统组件
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
#自定义View
-keep public class * extends android.view.View
# V4,V7
-keep class android.support.v4.**{ *; }
-keep class android.support.v7.**{ *; }
-keep class android.webkit.**{*;}
-keep interface android.support.v4.app.** { *; }
#保持 本化方法及其类声明
-keepclasseswithmembers class * {
    native <methods>;
}
#保持view的子类成员： getter setter
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
#保持Activity的子类成员：参数为一个View类型的方法   如setContentView(View v)
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#保持 任意包名.R类的类成员属性。  即保护R文件中的属性名不变
-keepclassmembers class **.R$* {
    public static <fields>;
}


#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}



-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}


-keep class com.umeng.** {*;}
-keepclassmembers class * {
       public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
        public static **[] values();
        public static ** valueOf(java.lang.String);
}

-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*
-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class com.calendarnews.R$*{
        public static final int *;
}

#greendao3.2.0,此是针对3.2.0，如果是之前的，可能需要更换下包名
-keep class org.greenrobot.greendao.**{*;}
 -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
  public static java.lang.String TABLENAME;
  }
 -keep class **$Properties


 -keepattributes *Annotation*
 -keepattributes RuntimeVisibleAnnotations
  -keepattributes RuntimeInvisibleAnnotations
  -keepattributes RuntimeVisibleParameterAnnotations
  -keepattributes RuntimeInvisibleParameterAnnotations
  -keepclassmembers class ** {
         @com.threshold.rxbus2.annotation.RxSubscribe <methods>;
   }
    -keep enum com.threshold.rxbus2.util.EventThread { *; }

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}