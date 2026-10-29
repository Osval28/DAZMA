package co.dazma.api.shared.infrastructure.input.controllers;

import co.dazma.api.shared.infrastructure.input.dto.MunicipalityResponseDto;
import co.dazma.api.shared.services.input.ports.ListMunicipalitiesInputPort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/shared/api/v1/municipalities")
public class MunicipalityController {

    private final ListMunicipalitiesInputPort listMunicipalities;

    public MunicipalityController(ListMunicipalitiesInputPort listMunicipalities) {
        this.listMunicipalities = listMunicipalities;
    }

    @GetMapping
    public ResponseEntity<List<MunicipalityResponseDto>> list() {
        List<MunicipalityResponseDto> body = listMunicipalities.execute().stream()
                .map(MunicipalityResponseDto::from)
                .toList();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .body(body);
    }
}
