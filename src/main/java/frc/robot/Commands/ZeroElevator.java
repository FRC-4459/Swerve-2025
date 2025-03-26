package frc.robot.Commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Subsystems.*;


public class ZeroElevator extends Command {
    private ElevatorSubsystem elevator;

    public ZeroElevator(ElevatorSubsystem elevator) {
        this.elevator = elevator;
        addRequirements(elevator);
    }

    @Override
    public void initialize() {
        elevator.setElevatorSpeed(-0.2, false);
    }

    @Override
    public void execute() {
        if (elevator.getElevatorEncoder().getVelocity() <= 0) {
            elevator.setElevatorSpeed(0, false);
            elevator.zeroEncoders();
            this.cancel();
        }
    }
    
}
