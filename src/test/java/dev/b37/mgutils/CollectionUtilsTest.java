package dev.b37.mgutils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CollectionUtilsTest {
    @Test
    public void testMapByKey() {
        List<Person> people = Arrays.asList(
                new Person(UUID.randomUUID(), "One 1"),
                new Person(UUID.randomUUID(), "Two 2"),
                new Person(UUID.randomUUID(), "Three 3"),
                new Person(UUID.randomUUID(), "Four 4"),
                new Person(UUID.randomUUID(), "Five 5"),
                new Person(UUID.randomUUID(), "Six 6"),
                new Person(UUID.randomUUID(), "Seven 7"),
                new Person(UUID.randomUUID(), "Eight 8"),
                new Person(UUID.randomUUID(), "N9ne"),
                new Person(UUID.randomUUID(), "Ten 10")
        );

        Map<UUID, Person> peopleMap = CollectionUtils.mapByKey(people, Person::getId);

        Assertions.assertEquals(people.size(), peopleMap.size());

        for (Person person : people) {
            Assertions.assertEquals(person, peopleMap.get(person.getId()));
        }

        for (UUID personId : peopleMap.keySet()) {
            Assertions.assertEquals(personId, peopleMap.get(personId).getId());
        }

        Person lastPerson = people.get(people.size() - 1);

        List<Person> people2 = new ArrayList<>(people);

        people2.add(new Person(lastPerson.getId(), "Ten 10 Duplicate"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CollectionUtils.mapByKey(people2, Person::getId);
        });

        Assertions.assertEquals(people2.size(), CollectionUtils.mapByKey(people2, Person::getName).size());
    }

    @Test
    public void testMapByKeyArray() {
        Person[] people = new Person[] {
                new Person(UUID.randomUUID(), "One 1"),
                new Person(UUID.randomUUID(), "Two 2"),
                new Person(UUID.randomUUID(), "Three 3"),
                new Person(UUID.randomUUID(), "Four 4"),
                new Person(UUID.randomUUID(), "Five 5"),
                new Person(UUID.randomUUID(), "Six 6"),
                new Person(UUID.randomUUID(), "Seven 7"),
                new Person(UUID.randomUUID(), "Eight 8"),
                new Person(UUID.randomUUID(), "N9ne"),
                new Person(UUID.randomUUID(), "Ten 10")
        };

        Map<UUID, Person> peopleMap = CollectionUtils.mapByKey(people, Person::getId);

        Assertions.assertEquals(people.length, peopleMap.size());

        for (Person person : people) {
            Assertions.assertEquals(person, peopleMap.get(person.getId()));
        }

        for (UUID personId : peopleMap.keySet()) {
            Assertions.assertEquals(personId, peopleMap.get(personId).getId());
        }
    }

    @Test
    public void testMapByKeyCustomMap() {
        List<Person> people = Arrays.asList(
                new Person(UUID.randomUUID(), "One 1"),
                new Person(UUID.randomUUID(), "Two 2"),
                new Person(UUID.randomUUID(), "Three 3"),
                new Person(UUID.randomUUID(), "Four 4"),
                new Person(UUID.randomUUID(), "Five 5"),
                new Person(UUID.randomUUID(), "Six 6"),
                new Person(UUID.randomUUID(), "Seven 7"),
                new Person(UUID.randomUUID(), "Eight 8"),
                new Person(UUID.randomUUID(), "N9ne"),
                new Person(UUID.randomUUID(), "Ten 10")
        );

        Map<UUID, Person> peopleMap = CollectionUtils.mapByKey(people, Person::getId, LinkedHashMap::new);
        Assertions.assertInstanceOf(LinkedHashMap.class, peopleMap);

        Assertions.assertEquals(people.size(), peopleMap.size());

        for (Person person : people) {
            Assertions.assertSame(person, peopleMap.get(person.getId()));
        }

        for (UUID personId : peopleMap.keySet()) {
            Assertions.assertEquals(personId, peopleMap.get(personId).getId());
        }
    }

    @Test
    public void testDefaultIfEmpty() {
        List<Object> fallback = Arrays.asList(1, 2, 3);

        List<Object> emptyList = Arrays.asList();
        List<Object> nonEmptyList = Arrays.asList(1, 2);

        Assertions.assertSame(fallback, CollectionUtils.defaultIfEmpty(null, fallback));
        Assertions.assertSame(fallback, CollectionUtils.defaultIfEmpty(emptyList, fallback));
        Assertions.assertNull(CollectionUtils.nullIfEmpty(emptyList));
        Assertions.assertSame(nonEmptyList, CollectionUtils.defaultIfEmpty(nonEmptyList, fallback));
        Assertions.assertNull(CollectionUtils.nullIfEmpty(null));
        Assertions.assertSame(nonEmptyList, CollectionUtils.nullIfEmpty(nonEmptyList));
    }

    private static class Person {
        private UUID id;
        private String name;

        public Person() {
        }

        public Person(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("id", id)
                    .append("name", name)
                    .toString();
        }
    }
}
