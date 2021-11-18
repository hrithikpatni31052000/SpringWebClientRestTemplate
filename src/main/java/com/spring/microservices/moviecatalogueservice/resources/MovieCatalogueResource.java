package com.spring.microservices.moviecatalogueservice.resources;

import com.spring.microservices.moviecatalogueservice.model.CatalogItem;
import com.spring.microservices.moviecatalogueservice.model.Movie;
import com.spring.microservices.moviecatalogueservice.model.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogueResource
{
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;


    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId)
    {
        //get all rated movie IDs
        UserRating ratings = restTemplate.getForObject("http://localhost:8084/ratingsdata/users/" + userId , UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
                    // for each ,movie ID , call movie info service and get details
            Movie movie = restTemplate.getForObject("http://localhost:8085/movies/" + rating.getMovieId() , Movie.class );

            /*
                    Movie movie = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8085/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)
                            .block();
                    */
                    //put them all together
                    return new CatalogItem(movie.getName() , "Desc" , rating.getRating());
        })
                .collect(Collectors.toList());
    }
}
