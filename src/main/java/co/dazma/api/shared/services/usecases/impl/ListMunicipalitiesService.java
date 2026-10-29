package co.dazma.api.shared.services.usecases.impl;

import co.dazma.api.shared.domain.CatalogedMunicipality;
import co.dazma.api.shared.services.output.ports.MunicipalityCatalogPort;
import co.dazma.api.shared.services.usecases.contract.ListMunicipalitiesUseCase;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ListMunicipalitiesService implements ListMunicipalitiesUseCase {

    private final MunicipalityCatalogPort catalog;

    public ListMunicipalitiesService(MunicipalityCatalogPort catalog) {
        this.catalog = catalog;
    }

    @Override
    public List<CatalogedMunicipality> execute() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es-CO"));
        return catalog.findAll().stream()
                .sorted(Comparator.comparing(CatalogedMunicipality::name, collator))
                .toList();
    }
}
