package frc.robot.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Subsystems.ElasticDashboard;

public class UpdateElastic extends Command {
    private final ElasticDashboard dashboard;
    private double[] vals = {99.0, 99.0, 99.0, 99.0};

    public UpdateElastic(ElasticDashboard dashboard) {
        this.dashboard = dashboard;
        addRequirements(dashboard);
    }

    @Override
    public void initialize() {
        dashboard.updateEncoderValues(vals);
        System.out.println("omg im sooooooo initialized haiii");
    }

    @Override
    public void execute() {
        dashboard.updateEncoderValues(vals);
        System.out.println("omg im sooooooo executed haiii");
    }

    @Override
    public boolean runsWhenDisabled() { return false; }
    
    @Override
    public boolean isFinished() { return false; }

    public void setEncoderValues(double[] encoderVals) {
       vals[0] = encoderVals[0]; 
       vals[1] = encoderVals[1]; 
       vals[2] = encoderVals[2]; 
       vals[3] = encoderVals[3]; 
    }
}
