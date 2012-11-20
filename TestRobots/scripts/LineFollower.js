importPackage(Packages.it.uniroma1.di.simulejos.math);

var wheelSpan = robot.boundingBox.size.x;
var wheelDiameter = robot.boundingBox.size.y;

robot.S1.lightSensor(
	new Vector3(
		robot.boundingBox.center.x,
		robot.boundingBox.min.y + 0.1,
		robot.boundingBox.max.z + 0.1
		),
	Matrix3.create([1, 0, 0, 0, 0, -1, 0, 1, 0])
	);

function tick(daa, dab, dac) {
	daa /= 3;
	dab /= 3;
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
