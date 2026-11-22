export async function postJson(path, data, fallbackMessage) {
    const response = await fetch(path, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    });

    if (!response.ok) {
        const failure = await response.json().catch(() => null);

        throw new Error(
            failure?.erros?.join(' ') || fallbackMessage,
        );
    }

    return response.json();
}

export async function getJson(path, signal, fallbackMessage) {
    const response = await fetch(path, { signal });

    if (!response.ok) {
        throw new Error(fallbackMessage);
    }

    return response.json();
}