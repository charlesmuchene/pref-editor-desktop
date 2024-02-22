# Keep all PainterHints
-keep interface * extends org.jetbrains.jewel.ui.painter.PainterHint

# Keep XmlPullParser implementation
-keep class org.kxml2.io.* { *; }

# Keep Slf4j implementation
-keep class org.slf4j.simple.Simple* { *; }

# Editor
-keep class com.charlesmuchene.prefeditor.** { *; }

# JBR
# -keep class com.jetbrains.**

# Playing with proguard
# -ignorewarnings
# -printconfiguration