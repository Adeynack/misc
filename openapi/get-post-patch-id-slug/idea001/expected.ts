type UUID = string // for the example

export interface PersonPatch {
    firstName?: string
    lastName?: string
    birthdate?: Date
    description?: string
}

export interface PersonReq {
    firstName: string           // required
    lastName: string            // required 
    birthdate: Date             // required
    description?: string        // optional
}

export interface PersonCreate {
    // optional slug
    slug?: string
    // everything from `PersonReq`, with some required.
    firstName: string
    lastName: string
    birthdate: Date
    description?: string
}

export interface Person {
    // required ID
    id: UUID
    // required slug
    slug: string
    // everything from `PersonReq`, with some required.
    firstName: string
    lastName: string
    birthdate: Date
    description?: string
}

class PersonAPI {

    getPersonList(): Array<Person> {
        // GET request to `/persons`
        return [];
    }

    createPerson(person: PersonCreate): Person {
        // POST request to `/persons`
        // with person as the request body.
        return null;
    }

    getPerson(personSlug: string): Person {
        // GET request to `/person/${personSlug}`
        return null;
    }

    updatePerson(personSlug: string, personPatch: PersonPatch): Person {
        // PATCH Request to `person/${personSlug}`
        // with personPatch as the request body
        return null;
    }

}
