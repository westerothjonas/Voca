$dest = 'C:\Users\CWest\AndroidStudioProjects\Voca\.artifacts\extracted_stitches'
New-Item -ItemType Directory -Force -Path $dest
Get-ChildItem -Path 'C:\Users\CWest\Downloads' -Filter 'stitch_vokabeltrainer_men_und_paketverwaltung*.zip' | ForEach-Object {
    $target = Join-Path $dest $_.BaseName
    Expand-Archive -Path $_.FullName -DestinationPath $target -Force
    Write-Host "Extracted $_ to $target"
}
