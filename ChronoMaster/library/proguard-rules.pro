# Add specific rules for the library here.
# For example, you might want to keep certain classes or methods
# that are accessed via reflection.
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keep class com.chronomaster.** { *; }
