package frc.robot.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Subsystems.*;

import edu.wpi.first.wpilibj.Timer;

public class RunElevator extends Command {
    private ElevatorSubsystem elevator;
    private double s;
    private boolean timed = false;
    private Timer timer;
    private double time;

    public RunElevator(ElevatorSubsystem elevator, double speed) {
        this.elevator = elevator;
        this.s = speed;
        addRequirements(elevator);
    }

    public RunElevator(ElevatorSubsystem elevator, double speed, double time) {
        this.elevator = elevator;
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
    }

    @Override
    public void execute() {
        elevator.setElevatorSpeed(s, true);
    }

    @Override
    public void end(boolean interruped) {
        elevator.setElevatorSpeed(0, true);
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
