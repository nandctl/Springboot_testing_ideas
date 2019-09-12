package io.tpd.superheroes.repository;

import io.tpd.superheroes.domain.SuperHero;
import io.tpd.superheroes.exceptions.NonExistingHeroException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
class SuperHeroRepositoryImpl implements SuperHeroRepository {

    private List<SuperHero> superHeroList;

    public SuperHeroRepositoryImpl() {
        superHeroList = new ArrayList<>();
        superHeroList.add(new SuperHero("Karunkar", "reddy", "Banglore"));
        superHeroList.add(new SuperHero("Praba", "Sundaram", "Banglore"));
        superHeroList.add(new SuperHero("Vaidy", "Rappal", "Newyork"));
        superHeroList.add(new SuperHero("Yana", "Wu", "St.louis"));
        superHeroList.add(new SuperHero("Rao", "Pallekala", "St.louis"));
        superHeroList.add(new SuperHero("Srinath", "koneru", "St.louis"));
        superHeroList.add(new SuperHero("Bailey", "Todd", "St.louis"));
        superHeroList.add(new SuperHero("Nand", "Rai", "St.louis"));


    }

    @Override
    public SuperHero getSuperHero(int id) {
        if (id > superHeroList.size()) throw new NonExistingHeroException();
        return superHeroList.get(id - 1);
    }

    @Override
    public Optional<SuperHero> getSuperHero(String heroName) {
        return superHeroList.stream().filter(h -> h.getCity().equals(heroName)).findAny();
    }

    @Override
    public void saveSuperHero(SuperHero superHero) {
        superHeroList.add(superHero);
    }
}
