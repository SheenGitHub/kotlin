/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.refactoring.changeSignature.usages

import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageInfo
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinChangeInfo
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall

abstract class JavaMethodKotlinUsageWithDelegate<T: PsiElement>(
        val psiElement: T,
        var javaMethodChangeInfo: KotlinChangeInfo): UsageInfo(psiElement) {
    abstract val delegateUsage: KotlinUsageInfo<T>

    fun processUsage(allUsages: Array<UsageInfo>): Boolean = delegateUsage.processUsage(javaMethodChangeInfo, psiElement, allUsages)
}

class JavaMethodKotlinCallUsage(
        callElement: KtCallElement,
        javaMethodChangeInfo: KotlinChangeInfo,
        propagationCall: Boolean
): JavaMethodKotlinUsageWithDelegate<KtCallElement>(callElement, javaMethodChangeInfo) {
    @Suppress("UNCHECKED_CAST")
    override val delegateUsage = when {
        propagationCall -> KotlinCallerCallUsage(psiElement)
        psiElement is KtConstructorDelegationCall -> KotlinConstructorDelegationCallUsage(psiElement, javaMethodChangeInfo)
        else -> KotlinFunctionCallUsage(psiElement, javaMethodChangeInfo.methodDescriptor.originalPrimaryCallable)
    } as KotlinUsageInfo<KtCallElement>
}
