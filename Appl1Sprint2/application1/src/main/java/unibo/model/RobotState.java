package unibo.model;

import unibo.common.IRobotState;
import unibo.basicomm23.utils.CommUtils;

public class RobotState implements IRobotState {

    private int x;
    private int y;
    private Direction direction;
    private RoomModel roomModel = RoomModel.getRoomModel();
    private static RobotState singletonRoomModel;

    // constructor
    public RobotState( int x, int y, Direction direction ) {
        if ( x < 0 || y < 0 || (direction != Direction.UP &&
                direction != Direction.RIGHT && direction != Direction.DOWN
                && direction != Direction.LEFT ))
            throw new IllegalArgumentException();

        this.x = x;
        this.y = y;
        this.direction = direction;
        roomModel.put(x, y, new Box(false, false, true));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        RobotState state = (RobotState) o;
        return this.x == state.x && this.y == state.y && this.direction == state.direction;
    }


    // enum Direction { UP, RIGHT, DOWN, LEFT; }
    @Override
    public Direction getDirection() {
        return this.direction;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public static RobotState getRobotState() {
        if ( singletonRoomModel == null ) {
            singletonRoomModel = new RobotState(0, 0, Direction.DOWN);
        }
        return singletonRoomModel;
    }

    // switch direction
    @Override
    public void turnRight() {
        switch(direction) {
            case UP:
                direction = Direction.RIGHT;
                break;
            case RIGHT:
                direction = Direction.DOWN;
                break;
            case DOWN:
                direction = Direction.LEFT;
                break;
            case LEFT:
                direction = Direction.UP;
                break;
        }
    }

    @Override
    public void turnLeft() {
        switch(direction) {
            case UP:
                direction = Direction.LEFT;
                break;
            case RIGHT:
                direction = Direction.UP;
                break;
            case DOWN:
                direction = Direction.RIGHT;
                break;
            case LEFT:
                direction = Direction.DOWN;
                break;
        }
    }

    @Override
    public void forward() {
        clearCurrentPos();
        switch(direction) {
            case UP:
                y--;
                if ( y < 0 )
                    y=0;
                break;
            case DOWN:
                y++;
                break;
            case LEFT:
                x--;
                if( x < 0 )
                    x=0;
                break;
            case RIGHT:
                x++;
                break;
            default:
                return ;
        }
        changePosInRoom(x,y);
    }

    protected void changePos(int x, int y, Direction dir, RoomModel room){
        this.x = x;
        this.y = y;
        this.direction = dir;
        room.put(x,y, new Box(false,false,true));
    }
    protected void changePosInRoom( int x, int y ){
        this.x = x;
        this.y = y;
        roomModel.put(x,y, new Box(false,false,true));
    }

    protected void clearCurrentPos(){
        CommUtils.outyellow( "RobotState clearCurrentPos: x=" + x + " y="+y );
        roomModel.put(x,y, new Box(false,true,false));
    }

    protected void changeDirectionInRoom(  Direction dir ){
        this.direction = dir;
        roomModel.put(x,y, new Box(false,false,true));
    }

    @Override
    public void backward() {
        clearCurrentPos();
        switch(direction) {
            case UP:
                y++;
                break;
            case DOWN:
                y--;
                if( y<0 )
                    y=0;
                break;
            case LEFT:
                x++;
                break;
            case RIGHT:
                x--;
                if( x<0 )
                    x=0;
                break;
            default:
                return;//throw new IllegalArgumentException("Direction not valid");
        }
        changePosInRoom(x,y);
    }



    @Override
    public Direction getBackwardDirection() {
        switch(direction) {
            case UP:
                return Direction.DOWN;
            case RIGHT:
                return Direction.LEFT;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            default:
                return direction;
        }
    }

    public String toString() {

        return roomModel.toString()+"RobotPos= ("+x+","+y+") direction= "+direction;
    }

}
