var wheelSpan = 2;
var wheelDiameter = 1;

function tick(daa, dab, dac) {
	var dsa = daa * wheelDiameter * Math.PI;
	var dsb = dab * wheelDiameter * Math.PI;
	if (Math.abs(dsa) > Math.abs(dsb)) {
		var angle = (dsa - dsb) / wheelSpan;
		var radius = dsa * wheelSpan / (dsa - dsb) - wheelSpan / 2;
		robot.moveBy(radius - Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
		robot.rotateBy(0, 1, 0, -angle);
	} else if (Math.abs(dsb) > Math.abs(dsa)) {
		var angle = (dsb - dsa) / wheelSpan;
		var radius = dsb * wheelSpan / (dsb - dsa) - wheelSpan / 2;
		robot.moveBy(Math.cos(angle) * radius - radius, 0, Math.sin(angle) * radius);
		robot.rotateBy(0, 1, 0, angle);
	} else {
		robot.moveBy(0, 0, dsa);
	}
}
