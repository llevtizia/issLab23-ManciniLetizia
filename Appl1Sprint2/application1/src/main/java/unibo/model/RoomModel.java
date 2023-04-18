package unibo.model;

import unibo.basicomm23.utils.CommUtils;
import unibo.common.IBox;
import unibo.common.IRoomModel;

import java.util.ArrayList;
import java.util.List;

public class RoomModel implements IRoomModel {

    private static RoomModel singletonRoomModel;
    // array (Y) di array di box (X)
    private List<ArrayList<IBox>> roomMap = new ArrayList<ArrayList<IBox>>();

    private RoomModel( ) {
        this.put(0, 0, new Box());
    }

    public static RoomModel getRoomModel( ){
        if ( singletonRoomModel == null )
            singletonRoomModel = new RoomModel();
        return singletonRoomModel;
    }

    @Override
    public int getDimX() {
        int result = 0;
        for ( int i = 0; i < roomMap.size(); i++ ) {
            if ( result < roomMap.get(i).size())
                result = roomMap.get(i).size();
        }
        return result;
    }

    @Override
    public int getDimY() {
        return roomMap.size();
    }

    @Override
    public void put(int x, int y, IBox box) {
        try {
            roomMap.get(y);
        } catch (IndexOutOfBoundsException e) {
            for (int i = roomMap.size(); i < y; i++)
                roomMap.add(new ArrayList<IBox>());
            roomMap.add(y, new ArrayList<IBox>());
        }

        try {
            roomMap.get(y).get(x);
            roomMap.get(y).remove(x);
            roomMap.get(y).add(x, box);
        } catch (IndexOutOfBoundsException e) {
            for ( int j = roomMap.get(y).size(); j < x; j++ )
                roomMap.get(y).add(new Box());
            roomMap.get(y).add(x, box);
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ArrayList<IBox> a : roomMap) {
            builder.append("|");
            for (IBox b : a) {
                if (b == null)
                    break;
                if (b.isRobot())
                    builder.append("r, ");
                else if (b.isObstacle())
                    builder.append("X, ");
                else if (b.isFree())
                    builder.append("1, ");
                else
                    builder.append("0, ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public String toStringFlat(){

        return toString().replace("\n","@");
    }

    //Just to test ....
    public static void main(String[] args){
        RoomModel room = RoomModel.getRoomModel();
        CommUtils.outblue( room.toString() );
/*
        for(int i=1; i<=3; i++) room.put(i,0, new Box() );  //Y
        for(int i=1; i<=3; i++) room.put(0,i, new Box() );  //X
        for(int i=4; i<=5; i++) room.put(0,i, new Box(true,false,false) );  //X
        CommUtils.outblue( room.toString() );
*/
        room.put(5,3, new Box(false, true, false));
        CommUtils.outblue( room.toString() );
        CommUtils.outblue( "X dim = " + room.getDimX() );
        CommUtils.outblue( "Y dim = " + room.getDimY() );
    }



}
