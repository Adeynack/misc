type UUID = string // for the example

type Translated = Map<String, String>

export interface PersonPatch {
    // everything is optional
    firstName?: string
    lastName?: string
    birthdate?: Date
    description?: string
    originalName?: Translated
}

export interface PersonReq {
    firstName: string           // required
    lastName: string            // required 
    birthdate: Date             // required
    description?: string        // optional
    originalName?: Translated   // optional
}

export interface PersonCreate {
    // optional slug
    slug?: string
    // everything from `PersonReq`, with some required.
    firstName: string
    lastName: string
    birthdate: Date
    description?: string
    originalName?: Translated
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
    originalName?: Translated
}

export interface PersonList {
    data: Array<Person>
}

export interface PersonListItem {
    id: UUID
    slug: string
    firstName: String
    lastName: String
}

class PersonAPI {

    getPersonList(): Array<PersonListItem> {
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
