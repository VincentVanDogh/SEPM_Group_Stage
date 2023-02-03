package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ArtistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ArtistServiceImplementation implements ArtistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    public ArtistServiceImplementation(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
    }

    @Override
    public ArtistDto save(ArtistDto artistDto) {
        LOGGER.debug("save({})", artistDto);

        Artist artist = artistMapper.artistDtoToArtist(artistDto);
        artist = artistRepository.save(artist);

        return artistMapper.artistToArtistDto(artist);
    }

    @Override
    public Stream<ArtistDto> getAll() {
        LOGGER.debug("getAll()");
        return artistRepository.findAll().stream().map(artistMapper::artistToArtistDto);
    }

    @Override
    @Transactional
    public Stream<ArtistDto> searchByName(String searchParams) {
        LOGGER.debug("searchByName({})", searchParams);

        List<Artist> artists = artistRepository
            .findFirst10ByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrBandNameContainsIgnoreCaseOrStageNameContainsIgnoreCase(
                searchParams, searchParams, searchParams, searchParams);
        return artists.stream().map(artistMapper::artistToArtistDto);
    }
}
