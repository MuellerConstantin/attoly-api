package de.mueller_constantin.attoly.api.domain.impl;

import de.mueller_constantin.attoly.api.domain.DisposableEmailDomainService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DisposableEmailDomainServiceImpl implements DisposableEmailDomainService {
    private final Set<String> disposableDomains;

    public DisposableEmailDomainServiceImpl(
            @Value("classpath:disposable_domains.txt") Resource resource
    ) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            disposableDomains = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public boolean isDisposable(String email) {
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        return disposableDomains.contains(domain);
    }
}
