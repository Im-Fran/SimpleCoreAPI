dependencies {
    // No snakeyaml or gson types are exposed in the public API: both are relocated in the
    // universal jar so they do not clash with the server's own copies.
    implementation(libs.snakeyaml)
    implementation(libs.gson)
}
