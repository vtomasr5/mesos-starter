package com.containersolutions.mesos.scheduler;

import com.containersolutions.mesos.scheduler.config.MesosConfigProperties;
import org.apache.mesos.Protos;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandInfoMesosProtoFactory implements MesosProtoFactory<Protos.CommandInfo.Builder> {
    @Autowired
    MesosConfigProperties mesosConfig;

    @Override
    public Protos.CommandInfo.Builder create() {
        Protos.CommandInfo.Builder builder = Protos.CommandInfo.newBuilder();
        Optional<String> command = Optional.ofNullable(mesosConfig.getCommand());
        builder.setShell(command.isPresent());
        command.ifPresent(builder::setValue);
        Optional<List<String>> uris = Optional.ofNullable(mesosConfig.getUri());
        uris.ifPresent(list -> builder.addAllUris(list.stream().map(uri -> Protos.CommandInfo.URI.newBuilder().setValue(uri).build()).collect(Collectors.toList())));

        mesosConfig.getEnvironment().entrySet().stream()
                .map(kv -> Protos.Environment.Variable.newBuilder().setName(kv.getKey()).setValue(kv.getValue()).build())
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        variables -> builder.setEnvironment(Protos.Environment.newBuilder().addAllVariables(variables))));

        return builder;
    }
}
