package com.yaincoding.yaco_fashion.admin.es_dictionary.utils

import com.amazonaws.services.opensearch.AmazonOpenSearch
import com.amazonaws.services.opensearch.model.*
import org.springframework.stereotype.Component

@Component
class OpensearchPackageManager(
    private val opensearch: AmazonOpenSearch
) {

    fun createPackage(s3BucketName: String, packageName: String, s3Key: String): String {
        val request = CreatePackageRequest()
        request.packageName = packageName
        request.packageType = PackageType.TXTDICTIONARY.name
        request.packageSource = PackageSource().withS3BucketName(s3BucketName).withS3Key(s3Key)

        val result = opensearch.createPackage(request)
        return result.packageDetails.packageID
    }

    fun update(s3BucketName: String, packageId: String, s3Key: String) {
        val request = UpdatePackageRequest()
        request.packageID = packageId
        request.packageSource = PackageSource().withS3BucketName(s3BucketName).withS3Key(s3Key)
        opensearch.updatePackage(request)
    }

    fun waitForUpdate(packageId: String): String? {
        for(i: Int in 1..30) {
            val request = DescribePackagesRequest()
            request.withFilters(DescribePackagesFilter().withName("PackageID").withValue(listOf(packageId)))
            val result: DescribePackagesResult  = opensearch.describePackages(request)
            val packageDetails: PackageDetails = result.packageDetailsList.first()
            return if (packageDetails.packageStatus == PackageStatus.AVAILABLE.name) {
                packageDetails.availablePackageVersion
            } else if (packageDetails.packageStatus == PackageStatus.COPYING.name) {
                Thread.sleep(3000)
                continue
            } else {
                null
            }
        }
        return null
    }

    fun getPackageId(packageName: String): String? {
        val request = DescribePackagesRequest()
        request.withFilters(DescribePackagesFilter().withName("PackageName").withValue(listOf(packageName)))
        val result: DescribePackagesResult  = opensearch.describePackages(request)

        val packageDetails = result.packageDetailsList.firstOrNull()
        if (packageDetails != null) {
            return packageDetails.packageID
        }

        return null
    }

    fun associate(packageId: String, domainName: String) {
        val request: AssociatePackageRequest = AssociatePackageRequest()
        request.domainName = domainName
        request.packageID = packageId
        opensearch.associatePackage(request)
    }

    fun waitForAssociate(packageId: String, domainName: String, availablePackageVersion: String): Boolean {
        for (i: Int in 1..30) {
            val request = ListPackagesForDomainRequest()
            request.domainName = domainName
            val result: ListPackagesForDomainResult = opensearch.listPackagesForDomain(request)

            val packageDetailsList: List<DomainPackageDetails> = result.domainPackageDetailsList
            for (packageDetails in packageDetailsList) {
                if (packageDetails.packageID == packageId) {
                    return if (packageDetails.domainPackageStatus == DomainPackageStatus.ACTIVE.name
                        || packageDetails.domainPackageStatus == DomainPackageStatus.ASSOCIATING.name) {
                        true
                    } else if (packageDetails.domainPackageStatus == DomainPackageStatus.ASSOCIATION_FAILED.name) {
                        false
                    } else {
                        Thread.sleep(3000)
                        break
                    }
                }
            }
        }
        return false
    }

    fun getPackageStatus(domainName: String, packageName: String): String? {
        val packageId = getPackageId(packageName)
        val request = ListPackagesForDomainRequest()
        request.domainName = domainName
        val result: ListPackagesForDomainResult = opensearch.listPackagesForDomain(request)

        val packageDetailsList: List<DomainPackageDetails> = result.domainPackageDetailsList
        for (packageDetails in packageDetailsList) {
            if (packageDetails.packageID == packageId) {
                return packageDetails.domainPackageStatus
            }
        }

        return null
    }
}