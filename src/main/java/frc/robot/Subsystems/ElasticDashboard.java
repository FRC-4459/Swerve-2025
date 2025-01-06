package frc.robot.Subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ElasticDashboard extends SubsystemBase{
    private final NetworkTable dashboardTable;
    private final NetworkTableEntry topRightEncoder;
    private final NetworkTableEntry bottomRightEncoder;
    private final NetworkTableEntry bottomLeftEncoder;
    private final NetworkTableEntry topLeftEncoder;
    private double[] encoderVals = {99.0, 99.0, 99.0, 99.0};

    public ElasticDashboard() {
        dashboardTable = NetworkTableInstance.getDefault().getTable("ElasticDashboard");

        topRightEncoder = dashboardTable.getEntry("topRightEncoder");
        bottomRightEncoder = dashboardTable.getEntry("bottomRightEncoder");
        bottomLeftEncoder = dashboardTable.getEntry("bottomLeftEncoder");
        topLeftEncoder = dashboardTable.getEntry("topLeftEncoder");
    }

    @Override
    public void periodic() {
        topRightEncoder.setDouble(encoderVals[0]);
        bottomRightEncoder.setDouble(encoderVals[1]);
        bottomLeftEncoder.setDouble(encoderVals[2]);
        topLeftEncoder.setDouble(encoderVals[3]);
    }

    public void updateEncoderValues(double[] values) {
        encoderVals[0] = values[0];
        encoderVals[1] = values[1];
        encoderVals[2] = values[2];
        encoderVals[3] = values[3];
    }
}
