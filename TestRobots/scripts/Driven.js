importPackage(Packages.it.uniroma1.di.simulejos.math);

var wheelSpan = robot.boundingBox.size.x;
var wheelDiameter = robot.boundingBox.size.y;

robot.S1.compassSensor(Matrix3.create([1, 0, 0, 0, 1, 0, 0, 0, 1]));

function tick(daa, dab, dac) {
	var dsa = daa * wheelDiameter * Math.PI;
	var dsb = dab * wheelDiameter * Math.PI;
	if (dsa != dsb) {
		var radius = wheelSpan * (dsa + dsb) / (2 * (dsb - dsa));
		var angle = (dab - daa) * Math.PI;
		robot.moveBy(radius * (Math.cos(angle) - 1), 0, radius * Math.sin(angle));
		robot.rotateBy(0, 1, 0, angle);
	} else {
		robot.moveBy(0, 0, dsa);
	}
}
