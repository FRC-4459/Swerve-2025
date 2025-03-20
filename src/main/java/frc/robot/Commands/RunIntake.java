package frc.robot.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Subsystems.*;

import edu.wpi.first.wpilibj.Timer;

public class RunIntake extends Command {
    private IntakeSubsystem intake;
    private double s;
    private boolean timed = false;
    private Timer timer;
    private double time;

    public RunIntake(IntakeSubsystem intake, double speed) {
        this.intake = intake;
        this.s = speed;
        addRequirements(intake);
    }

    public RunIntake(IntakeSubsystem intake, double speed, double time) {
        this.intake = intake;
        this.s = speed;
        this.timed = true;
        this.timer = new Timer();
    }

    @Override
    public void initialize() {
        if (timed) {
            timer.reset();
            timer.start();
        }

        intake.run(s);
    }

    @Override
    public void execute() { }

    @Override
    public void end(boolean interruped) {
        intake.run(0);
    }

    @Override
    public boolean runsWhenDisabled() { return false; }
    
    @Override
    public boolean isFinished() { 
        if (timed) {
            if (time < timer.get()) {
                return true;
            }
        }
        
        return false; 
    }
}

