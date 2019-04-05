type UUID = string // for the example

type Translated = Map<String, String>

export interface PersonProps {
    // everything is optional
    firstName?: string
    lastName?: string
    birthdate?: Date
    description?: string
    originalName?: Translated
}

export interface PersonBase {
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

export interface PersonShow {
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

export interface PersonIndex {
    data: Array<PersonPartial>
}

export interface PersonPartial {
    id: UUID
    slug: string
    firstName: String
    lastName: String
}

class PersonAPI {

    getPersonList(): PersonIndex {
        // GET request to `/persons`
        return null;
    }

    createPerson(person: PersonCreate): PersonShow {
        // POST request to `/persons`
        // with person as the request body.
        return null;
    }

    getPerson(personSlug: string): PersonShow {
        // GET request to `/person/${personSlug}`
        return null;
    }

    updatePerson(personSlug: string, personPatch: PersonProps): PersonShow {
        // PATCH Request to `person/${personSlug}`
        // with personPatch as the request body
        return null;
    }

}
