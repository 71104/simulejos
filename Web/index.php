<!DOCTYPE html>
<html>
<head>
<title>Simulejos - Lejos Simulator</title>
<style type="text/css">
body {
	font-family: sans-serif;
}
</style>
<script type="text/javascript" src="http://www.java.com/js/deployJava.js"></script>
<script type="text/javascript">
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-33377050-1']);
_gaq.push(['_trackPageview']);
(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
</script>
</head>
<body>
<h1>Simulejos</h1>
<p>A 3D <a href="http://lejos.org/">Lejos</a> simulator by Alberto La Rocca. Sources on <a href="https://github.com/71104/simulejos">GitHub</a>.</p>
<h2>Requirements</h2>
<p>Simulejos requires Java SE 7 and OpenGL drivers: it runs on OpenGL 2.1 and uses GLSL 1.20.</p>
<h2>Download &amp; Installation</h2>
<p><script type="text/javascript">
deployJava.createWebStartLaunchButton('http://www.simulejos.altervista.org/Simulejos.jnlp', '1.7.0');
</script> or <a href="Simulejos.jnlp">Click here to run Simulejos</a></p>
<h2>Known Issues</h2>
<ul>
<li>The provided security certificate is self-signed.</li>
<li>At first, the program might not run if you are using Java 7. After launching the JNLP file Java Web Start will download it and complain about some stuff related to some certificate. This bug affects <em>all JOGL programs</em> and I can't do anything about it unfortunately, you have to manually adjust a Java security setting. Here's the steps in Windows 7:<ul>
<li>open "Java" in the Control Panel,</li>
<li>select the "Advanced" tab,</li>
<li>expand the "Security" section and the "General" subsection,</li>
<li>enable online certificate validation and join me in wondering why the heck was that disabled by default.</li>
</ul></li>
</ul>
<p><a href="mailto:a71104@gmail.com">Contact me</a> if you find out unknown issues.</p>
</body>
</html>
