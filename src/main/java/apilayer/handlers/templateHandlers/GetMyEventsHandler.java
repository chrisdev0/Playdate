package apilayer.handlers.templateHandlers;

import apilayer.StaticFileTemplateHandlerImpl;
import apilayer.handlers.asynchandlers.SparkHelper;
import dblayer.PlaydateDAO;
import model.Playdate;
import model.User;
import spark.Request;

import java.util.*;
import java.util.stream.Collectors;

public class GetMyEventsHandler extends StaticFileTemplateHandlerImpl {

    public GetMyEventsHandler() {
        super("my-events.vm", 400, true);
    }

}
