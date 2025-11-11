package com.example.agricom_it.repo;

import java.util.function.Consumer;

public class ChatListRepo
{
    public void findUserIdByIdentifier(String identifier, Consumer<Integer> callback)
    {
        // Example stub: replace with real lookup (e.g., Firebase query, REST call)
        new Thread(() ->
        {
            try
            {
                // simulate network lookup
                Thread.sleep(300);
            }
            catch (InterruptedException ignored)
            {

            }

            // TODO: perform actual lookup. For now, return -1 to indicate not found.
            // Example: if "alice" -> id 2
            Integer found = null;
            if ("alice".equalsIgnoreCase(identifier)) found = 2;
            else if ("bob@example.com".equalsIgnoreCase(identifier)) found = 3;

            callback.accept(found);
        }).start();
    }

}
