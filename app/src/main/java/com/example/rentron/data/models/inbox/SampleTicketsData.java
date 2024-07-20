package com.example.rentron.data.models.inbox;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.TicketEntityModel;
import com.example.rentron.data.sources.actions.InboxActions;

import java.util.ArrayList;
import java.util.List;

public class SampleTicketsData {

    public void createSampleTickets(int size) {
        List<TicketEntityModel> tickets = new ArrayList<>();

        String[] landlordIds = new String[] {
                "hPexzu45xjVBChIHuCgoHzn8vzY2",
                "rZfIpemX3CXQiqmc4t7KBpoYAuk1",
                "dNGgtlXhSrW9dnOZ7GNxUiexK5s2",
                "RiKxdx9zZNS8BmLBOFsA5W9oxgM2",
                "KcBOX85rKwfBO6WmrTITYyTYuH23",
                "rZfIpemX3CXQiqmc4t7KBpoYAuk1",
        };

        String[] clientIds = new String[] {
                "09byC6VCX3Qi3AvKG6NDEDypewO2",
                "9RFY3Qmge2V1iJ3TWKp4hTA6GHp2"
        };

        tickets.add(new TicketEntityModel("", "", "", "", "", null));
    }

    private void addTicket(String title, String desc, String dateSubmitted) {
        // App.getPrimaryDatabase().INBOX.addTicket();
    }

    public static void main(String[] args) {
        // Code to run the sample data creation
    }
}
