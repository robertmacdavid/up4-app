/*
 * Copyright 2020-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.up4;

import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.MacAddress;
import org.onlab.packet.Ip4Prefix;
import org.onlab.packet.Ip4Address;
import org.onosproject.cfg.ComponentConfigService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Dictionary;
import java.util.Properties;

import static org.onlab.util.Tools.get;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true,
           service = {SomeInterface.class},
           property = {
               "someProperty=Some Default String Value",
           })
public class AppComponent implements SomeInterface {




    private final Logger log = LoggerFactory.getLogger(getClass());

    /** Some configurable property. */
    private String someProperty;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected ComponentConfigService cfgService;

    @Activate
    protected void activate() {
        cfgService.registerProperties(getClass());
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        cfgService.unregisterProperties(getClass(), false);
        log.info("Stopped");
    }

    @Modified
    public void modified(ComponentContext context) {
        Dictionary<?, ?> properties = context != null ? context.getProperties() : new Properties();
        if (context != null) {
            someProperty = get(properties, "someProperty");
        }
        log.info("Reconfigured");
    }

    @Override
    public void someMethod() {
        log.info("Invoked");
    }


    enum Direction {
        UNKNOWN,
        UPLINK,
        DOWNLINK,
        BOTH,
    }

    enum InterfaceType {
        UNKNOWN         (0),
        ACCESS          (1),
        CORE            (2),
        N6_LAN          (3),  // unused
        VN_INTERNAL     (4),  // unused
        CONTROL_PLANE   (5);  // N4 and N4-u

        public final int value;

        private InterfaceType(int value) {
            this.value = value;
        }
    }

    private class RuleID {
        SessionID   sessionID;  // Session associated with this RuleID
        int         localID;    // Session-local Rule ID
        int         globalID;   // Globally unique Rule ID
    }

    private class SessionID {
        Ip4Address  endpoint;
        int         localID;
    }

    private class PdrTableEntry {
        /*
        Shared Info
            All UP4 match keys
            All UP4 action parameters
            Entry is for uplink OR downlink (determines table in fabric case, match key in UP4 case)
        UP4-only info - None
        Fabric-only Info
            Global PDR_ID
            Global FAR_ID
        */
        private Direction direction;
        private SessionID sessionID;
        private RuleID pdrID;
        private RuleID farID;

    }

    private class FarTableEntry {
        private SessionID sessionID;
        private RuleID farID;

        private boolean drop;
        private boolean tunnel;
    }

    private class InterfaceTableEntry {
        /*
        Shared Info
            Interface IP prefix (or exact address)
            Direction is uplink OR downlink (determines table in fabric case)
        UP4-only info
            Interface type

        */
        private Direction       direction;
        private InterfaceType   interfaceType;
        private Ip4Prefix       prefix;
    }

}
