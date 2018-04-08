package org.gatblau.onix;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.gatblau.onix.data.ItemData;
import org.gatblau.onix.model.Item;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api("ONIX CMDB Web API")
@RestController
public class WebAPI {

    @Autowired
    private Repository data;

    @ApiOperation(value = "Returns OK if the service is up and running.", notes = "Use it as a readiness probe for the service.", response = String.class)
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Info> index() {
        return ResponseEntity.ok(new Info("Onix Configuration Management Database Service.", "1.0"));
    }

    @ApiOperation(value = "Creates new item or updates an existing item based on the specified key.", notes = "")
    @RequestMapping(
            path = "/item/{key}/", method = RequestMethod.PUT,
            consumes = {"application/json" },
            produces = {"application/json" })
    public ResponseEntity<Result> createOrUpdateItem(
            @PathVariable("key") String key,
            @RequestBody JSONObject payload) throws InterruptedException, IOException {
        String action = data.createOrUpdateItem(key, payload);
        return ResponseEntity.ok(new Result(action));
    }

    @ApiOperation(value = "Creates new link or updates an existing link based on two existing item keys.", notes = "")
    @RequestMapping(
            path = "/link/{fromItemKey}/{toItemKey}/", method = RequestMethod.PUT,
            consumes = {"application/json" },
            produces = {"application/json" })
    public ResponseEntity<Result> createOrUpdateLink(
            @PathVariable("fromItemKey") String fromItemKey,
            @PathVariable("toItemKey") String toItemKey,
            @RequestBody JSONObject payload) throws InterruptedException, IOException {
        String action = data.createOrUpdateLink(fromItemKey, toItemKey, payload);
        return ResponseEntity.ok(new Result(action));
    }

    @RequestMapping(path = "/clear/", method = RequestMethod.DELETE)
    public void clear() throws InterruptedException {
        data.clear();
    }

    @RequestMapping(path = "/link/{fromItemKey}/{toItemKey}/", method = RequestMethod.DELETE)
    public void deleteLink(
            @PathVariable("fromItemKey") String fromItemKey,
            @PathVariable("toItemKey") String toItemKey
    ) throws InterruptedException {
        data.deleteLink(fromItemKey, toItemKey);
    }

    @RequestMapping(path = "/itemtype/", method = RequestMethod.DELETE)
    public void deleteItemTypes() throws InterruptedException {
        data.deleteItemTypes();
    }

    @ApiOperation(value = "Get a configuration item based on the specified key.", notes = "")
    @RequestMapping(
            path = "/item/{key}/", method = RequestMethod.GET,
            consumes = {"application/json" },
            produces = {"application/json" })
    public ResponseEntity<ItemData> getItem(@PathVariable("key") String key) {
        ItemData item = data.getItem(key);
        return ResponseEntity.ok(item);
    }
}